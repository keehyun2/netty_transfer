package kr.no1.zerocopy;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServerMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);
	private static final int CAPACITY = 1024 * 1024 * 2; // 2 MB
	private static final int PORT = 8080;

	public static void main(String[] arguments) throws IOException, InterruptedException {
		LOGGER.info("ServerMain start!");

		ServerChannel sc = new ServerChannel(PORT, 8);

		sc.openSocketChannel((socketChannel) -> {
			try {
				Map<String, Object> map = readHashMap(socketChannel);
//				readStringBuf(socketChannel);
//				if(map.containsKey("TYPE") ){
//					String type = map.get("TYPE").toString();
//					switch (type){
//						case "FILE":
//							System.out.println("");
//							break;
//						case "file1":
//							break;
//						default:
//					}
//					readFileBuf(socketChannel);
//				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private static HashMap<String, Object> readHashMap(SocketChannel socketChannel) throws IOException {
		HashMap<String, Object> result = new HashMap<>();
		ByteBuffer buf = ByteBuffer.allocate(4);
		if (socketChannel.read(buf) > -1) {
			buf.flip();
			int size = buf.getInt();
//			LOGGER.info("HashMap<String, Object> 크기(byte) : {}", size);
			ByteBuffer buf2 = ByteBuffer.allocate(size);
			if (socketChannel.read(buf2) > -1) {
				buf2.flip();
				result = SerializationUtils.deserialize(buf2.array());
				LOGGER.info("해시맵 : {}, size {}", result, size);
			}
		}
		return result;
	}

	private static String readStringBuf(SocketChannel socketChannel) throws IOException {
		String result = "";
		ByteBuffer buf = ByteBuffer.allocate(4);
		if (socketChannel.read(buf) > -1) {
			buf.flip();
			int size = buf.getInt();
			LOGGER.info("문자열 크기(byte) : {}", size);
			ByteBuffer buf2 = ByteBuffer.allocate(size);
			if (socketChannel.read(buf2) > -1) {
				buf2.flip();
				result = new String(buf2.array(), StandardCharsets.UTF_8);
				LOGGER.info("문자열 : {}", result);
			}
		}
		return result;
	}

	private static int readFileBuf(SocketChannel socketChannel) throws IOException {
		int size = 0;
		ByteBuffer buffer = ByteBuffer.allocateDirect(CAPACITY);
		while (socketChannel.read(buffer) > 0) {
			//				fileChannel.write(buffer);
			buffer.flip();
			LOGGER.info(buffer.toString());
			size += buffer.limit();
			buffer.clear();
		}
		LOGGER.info("size : {} byte", size);
		return size;
	}
}
