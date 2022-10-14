package kr.no1.zerocopy;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ServerMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);
	private static final int CAPACITY = 1024 * 1024 * 2; // 2 MB
	private static final int PORT = 8080;

	public static void main(String[] arguments) throws IOException {
		LOGGER.info("ServerMain start!");
		StopWatch stopWatch = new StopWatch();
		while (true) {
			try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
				serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
				serverSocketChannel.configureBlocking(true);

				try (SocketChannel socketChannel = serverSocketChannel.accept()) {
					stopWatch.start();
					LOGGER.info("Accepted : {}", socketChannel);

					readStringBuf(socketChannel);

//					int size = readFileBuf(socketChannel);

					stopWatch.stop();
					LOGGER.info("실행시간 : {} milliseconds", stopWatch.getTime());
					stopWatch.reset();
				}
			}
		}
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
