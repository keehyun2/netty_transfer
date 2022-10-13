package kr.no1.zerocopy;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerMain {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);
	private static final int PORT = 8080;
	private static final int CAPACITY = 1024 * 1024 * 1; // 1 MB

	public static void main(String[] arguments) throws IOException {
		LOGGER.info("ServerMain start!");
		StopWatch stopWatch = new StopWatch();

		while (true) {
			int size = 0;
			try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
				serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
				serverSocketChannel.configureBlocking(true);

				ByteBuffer buffer = ByteBuffer.allocateDirect(CAPACITY);

				try (SocketChannel socketChannel = serverSocketChannel.accept()) {
					stopWatch.start();
					LOGGER.info("Accepted : {}", socketChannel);

					while (socketChannel.read(buffer) > 0) {
						//				fileChannel.write(buffer);
						buffer.flip();
						LOGGER.info(buffer.toString());
						size += buffer.limit();
						buffer.clear();
					}

					stopWatch.stop();
					LOGGER.info("실행시간 : {} milliseconds, size: {} byte", stopWatch.getTime(), size);
					stopWatch.reset();
				}
			}


		}
	}
}
