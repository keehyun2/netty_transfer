package kr.no1.zerocopy;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientChannel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientChannel.class);
	private final String host;
	private final int port;

	private final List<ByteBuffer> byteBuffers = new ArrayList<>();

	// byte array 로 변환해서 보낼 목록 - 타입에 따라서 보내는 방식이 달라짐.
	public final List<Object> objectList = new ArrayList<>();

	public ClientChannel(String host, int port) {
		this.host = host;
		this.port = port;
	}


//	public int readyBuff(Path path) {
////		byte[] byteArr = SerializationUtils.serialize(hashMap);
////		ByteBuffer buff = convertToBuffer(byteArr);
////		byteBuffers.add(buff);
//
//		try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r");
//			 FileChannel fileChannel = randomAccessFile.getChannel()
//		) {
////			long transferSize = transfer(socketChannel, fileChannel);
//			int transferSize = 0;
//
//			// file buffer
//			LOGGER.info("fileChannel : {}, position: {}, size : {}", fileChannel, fileChannel.position(), fileChannel.size());
//			long size = fileChannel.size();
//			int position = 0;
//			while (size > 0) { // we still have bytes to transfer
//				long writeBytes = fileChannel.transferTo(position, size, socketChannel);
//				if (writeBytes > 0) {
//					LOGGER.info("writeBytes : {}", writeBytes);
//					position += writeBytes; // seeking position to last byte transferred
//					size -= writeBytes; // {count} bytes have been transferred, remaining {size}
//				}
//				transferSize += Math.max(writeBytes, 0);
//			}
//
//			LOGGER.info("transferSize : {}", transferSize);
//		} catch (FileNotFoundException e) {
//			LOGGER.error("ERROR", e);
//		} catch (IOException e) {
//			LOGGER.error("ERROR", e);
//		}
//
//		return buff.capacity();
//	}

	public int flushBuffer() {
		try (SocketChannel socketChannel = SocketChannel.open()) {
			socketChannel.connect(new InetSocketAddress(host, port));
			socketChannel.configureBlocking(true);
			int result = 0;
//			for (ByteBuffer buf : byteBuffers) {
//				result += socketChannel.write(buf);
//			}
			for (Object obj : objectList) {
				byte[] byteArr;
				ByteBuffer buff;
				switch (obj) {
					case HashMap map :
						byteArr = SerializationUtils.serialize(map);
						buff = convertToBuffer(byteArr);
						result += socketChannel.write(buff);
						break;
					case String str :
						// 문자 전송 전송
						byteArr = str.getBytes(StandardCharsets.UTF_8);    // string 을 byte[] 로 변환
						buff = convertToBuffer(byteArr);
						result += socketChannel.write(buff);
						break;
					case Path path :
						// 파일 전송
						break;
					default :
						break;
				};
			}
			return result;
		} catch (IOException e) {
			LOGGER.error("Send error", e);
			return -1;
		}
	}

	/**
	 * byte[] 를 소켓 채널에서 사용할 java.nio.ByteBuffer 로 반환
	 * @param byteArr object(데이터)를 byte[]로 변환
	 * @return java.nio.ByteBuffer 를 반환
	 */
	private static ByteBuffer convertToBuffer(byte[] byteArr) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + byteArr.length); // ByteBuffer 생성
		byteBuffer.putInt(byteArr.length); // write in buffer
		byteBuffer.put(byteArr); // write in buffer
		byteBuffer.flip();
		return byteBuffer;
	}


//	public int sendBuff(HashMap<String, Object> hashMap) {
//		byte[] byteArr = SerializationUtils.serialize(hashMap);
//		ByteBuffer buff = convertToBuffer(byteArr);
//		return sendBuffer(buff);
//	}
//
//	public int sendBuff(String title) {
//		byte[] byteArr = title.getBytes(StandardCharsets.UTF_8);    // string 을 byte[] 로 변환
//		ByteBuffer buff = convertToBuffer(byteArr);
//		return sendBuffer(buff);
//	}

//	/**
//	 * socketChannel 에 buffer 를 보내고 보낸 byte[].length 를 반환
//	 */
//	private int sendBuffer(ByteBuffer buff) {
//		try (SocketChannel socketChannel = SocketChannel.open()) {
//			socketChannel.connect(new InetSocketAddress(host, port));
//			socketChannel.configureBlocking(true);
//			return socketChannel.write(buff);
//		} catch (IOException e) {
//			LOGGER.error("Send error", e);
//			return -1;
//		}
//	}
}
