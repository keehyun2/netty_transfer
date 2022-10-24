package kr.no1.channel;

import kr.no1.zerocopy.util.ReadSockUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {

	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
	private static final int CAPACITY = 1024 * 1024 * 1; // 2 MB

	public static void main(String[] args) throws IOException {
		LOGGER.info("Server start!");

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		long size = 0;
		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
			serverSocketChannel.socket().bind(new InetSocketAddress(20000));
			serverSocketChannel.configureBlocking(true);
			SocketChannel socketChannel = serverSocketChannel.accept(); // 새로운 연결이 들어올때까지 blocking 됨
			ByteBuffer buffer = ByteBuffer.allocateDirect(CAPACITY);
			while (socketChannel.read(buffer) > -1 ) {
				buffer.flip();
				size += buffer.limit();
				buffer.clear();
			}
			socketChannel.close();
		}

		stopWatch.stop();
		float time = stopWatch.getTime() / 1000f;
		String size2 = FileUtils.byteCountToDisplaySize(size);
		float speed = size / time / 1024f / 1024f * 8; // MB/s * 8bit = Mbps

		LOGGER.info("전송시간: {} s, 크기: {}({}), 속도(Mbps): {} ", time, size, size2, speed);
	}
}
