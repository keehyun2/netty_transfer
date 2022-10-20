package kr.no1.zerocopy;

import kr.no1.socket.ServerSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ClientMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientMain.class);
	private static final int CAPACITY = 1024 * 1024 * 2; // 2 MB

	private static final boolean ZERO_COPY = true;

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8080;

	public static void main(String[] arguments) {
		LOGGER.info("SenderUtil start! is zeroCopy? " + ZERO_COPY);

//		String path = "C:\\Users\\khpark\\Downloads\\postgl\\암임상 라이브러리 테이블 생성 POST-GL SCRIPT.sql";
//		String path = "C:\\Users\\khpark\\Downloads\\eGovFrameDev-4.0.0-Win-64bit.exe";
		String path = "C:\\Users\\khpark\\Downloads\\eclipse-inst-jre-win64.exe";

//		ClientMain.fileSend(path, ZERO_COPY);

		// 테스트 해시맵 생성
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("TYPE", "FILE");
//
//		ClientChannel cc = new ClientChannel(HOST, PORT);
//		// 전송할 버퍼 준비(전송은 아직 안함)
//		cc.objectList.add(map);
////		cc.objectList.add("한글");
//		// 소켓채널 생성하여 준비시킨 버퍼 한번에 전송
//		int sendSize = cc.flushBuffer();

//		LOGGER.info("SenderUtil end! size: {}", sendSize);

		List<Long> aList = LongStream.rangeClosed(1, 120).boxed().collect(Collectors.toList());

		aList.parallelStream().forEach(v -> {
			HashMap<String, Object> map = new HashMap<>();
			map.put("n", "FILE " + v);

			ClientChannel cc = new ClientChannel(HOST, PORT);
			cc.objectList.add(map);
			int sendSize = cc.flushBuffer();
			LOGGER.info("SenderUtil end! size: {}", sendSize);
		});
	}

	public static void fileSend(String testFilePath) {

		try (SocketChannel socketChannel = SocketChannel.open()) {
			socketChannel.connect(new InetSocketAddress(HOST, PORT));
			socketChannel.configureBlocking(true);

			try (RandomAccessFile randomAccessFile = new RandomAccessFile(testFilePath, "r");
				 FileChannel fileChannel = randomAccessFile.getChannel()
			) {
//				long transferSize = transfer(socketChannel, fileChannel);
			}

		} catch (IOException e) {
			LOGGER.error("Send error", e);
		}
	}

//	private static ByteBuffer writeMapBuf(HashMap hashMap) {
//		byte[] byteArr = SerializationUtils.serialize(hashMap);
//		return convertToBuffer(byteArr);
//	}
//
//	private static ByteBuffer writeStringBuf(String title) {
//		byte[] byteArr = title.getBytes(StandardCharsets.UTF_8);    // string 을 byte[] 로 변환
//		return convertToBuffer(byteArr);
//	}

//	private static long transfer(SocketChannel socketChannel, FileChannel fileChannel) throws IOException {
//		int transferSize = 0;
//
//		// file buffer
//		LOGGER.info("fileChannel : {}, position: {}, size : {}", fileChannel, fileChannel.position(), fileChannel.size());
//		long size = fileChannel.size();
//		int position = 0;
//		while (size > 0) { // we still have bytes to transfer
//			long writeBytes = fileChannel.transferTo(position, size, socketChannel);
//			if (writeBytes > 0) {
//				LOGGER.info("writeBytes : {}", writeBytes);
//				position += writeBytes; // seeking position to last byte transferred
//				size -= writeBytes; // {count} bytes have been transferred, remaining {size}
//			}
//			transferSize += Math.max(writeBytes, 0);
//		}
//
//		LOGGER.info("transferSize : {}", transferSize);
//		return transferSize;
//	}

}