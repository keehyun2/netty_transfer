package kr.no1.zerocopy;

import kr.no1.zerocopy.data.CompositeFile;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientChannel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientChannel.class);
	private final String host;
	private final int port;

	private static final String MAP = "MAP";
	private static final String FILE = "FILE";
	private static final String COMPOSITE_FILE = "COMPOSITE_FILE";
	private static final String STRING = "STRING";

	private final ArrayList<String> dataTypeList = new ArrayList<>();

	// byte array 로 변환해서 보낼 목록 - 타입에 따라서 보내는 방식이 달라짐.
	private final List<Object> dataList = new ArrayList<>();

	public ClientChannel(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * 보낼 데이터 준비
	 */
	public boolean write(Object obj) {
		switch (obj) {
			case HashMap ignored -> dataTypeList.add(MAP);
			case String ignored -> dataTypeList.add(STRING);
			case Path ignored -> dataTypeList.add(FILE);
			case CompositeFile ignored -> dataTypeList.add(COMPOSITE_FILE);
			default -> throw new IllegalStateException("Unexpected value: " + obj);
		}
		return dataList.add(obj);
	}

	/**
	 * 준비된 데이터 전송
	 */
	public int flushBuffer() {
		byte[] byteArr;
		ByteBuffer buff;
		int writeBytes = 0;

		try (SocketChannel socketChannel = SocketChannel.open()) {
			socketChannel.connect(new InetSocketAddress(host, port));
			socketChannel.configureBlocking(true);

			// 데이터 타입 목록
			byteArr = SerializationUtils.serialize(dataTypeList);
			buff = convertToBuffer(byteArr);
			writeBytes += socketChannel.write(buff);

			// 데이터
			for (Object data : dataList) {
				switch (data) {
//					case HashMap map -> { // HashMap 전송 전송
//						byteArr = SerializationUtils.serialize(map);
//						buff = convertToBuffer(byteArr);
//						writeBytes += socketChannel.write(buff);
//					}
					case String str -> { // 문자 전송 전송
						byteArr = str.getBytes(StandardCharsets.UTF_8);    // string 을 byte[] 로 변환
						buff = convertToBuffer(byteArr);
						writeBytes += socketChannel.write(buff);
					}
					case Path path -> { // 파일 전송
						writeBytes += sendFile(socketChannel, path);
					}
					case CompositeFile cFile -> { // Composite File 전송 전송
						byteArr = SerializationUtils.serialize(cFile);
						buff = convertToBuffer(byteArr);
						writeBytes += socketChannel.write(buff);


						writeBytes += sendFile(socketChannel, Paths.get(cFile.sourceFile()));
					}
					default -> throw new IllegalStateException("Unexpected value: " + data);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Send error", e);
			return -1;
		}
		return writeBytes;
	}

	private ByteBuffer convertToBuffer(byte[] byteArr) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + byteArr.length); // ByteBuffer 생성
		byteBuffer.putInt(byteArr.length); // write in buffer
		byteBuffer.put(byteArr); // write in buffer
		byteBuffer.flip();
		return byteBuffer;
	}

	private long sendFile(SocketChannel socketChannel, Path path){
		long writeBytes = 0;
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r");
			 FileChannel fileChannel = randomAccessFile.getChannel()
		) {
			long fileSize = fileChannel.size();
			ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
			byteBuffer.putLong(fileSize);
			byteBuffer.flip();
			socketChannel.write(byteBuffer);
			// file buffer
			LOGGER.info("fileChannel : {}, position: {}, size : {}", fileChannel, fileChannel.position(), fileSize);
			int position = 0;
			while (fileSize > 0) { // we still have bytes to transfer
				long transferBytes = fileChannel.transferTo(position, fileSize, socketChannel);
				if (transferBytes > 0) {
					LOGGER.debug("transferBytes : {}", transferBytes);
					position += transferBytes; // seeking position to last byte transferred
					fileSize -= transferBytes;
					writeBytes += transferBytes;
				}
			}
			LOGGER.info("FILE size(BYTE) : {}", fileChannel.size());
		} catch (FileNotFoundException e) {
			LOGGER.error("ERROR FileNotFoundException", e);
		} catch (IOException e) {
			LOGGER.error("ERROR IOException", e);
		}
		return writeBytes;
	}

}
