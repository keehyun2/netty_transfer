package kr.no1.channel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Client {

	private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
	private static final int CAPACITY = 1024 * 1024 * 2; // 2 MB
	private static final long size = 5368709120L; // 5 GB

	public static void main(String[] args) throws IOException {
		LOGGER.info("Client start!");
		try (SocketChannel socketChannel = SocketChannel.open()) {
			socketChannel.connect(new InetSocketAddress("127.0.0.1", 20000));
//			socketChannel.connect(new InetSocketAddress("172.24.180.229", 20000));
			socketChannel.configureBlocking(true);

			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			long sendSize = 0;

			ByteBuffer byteBuffer = ByteBuffer.allocate(CAPACITY);
			while(size > sendSize){
				int i = 0;
				while(CAPACITY > i){
					byteBuffer.put((byte) (i/128));
					i++;
				}
				byteBuffer.flip();
				sendSize += socketChannel.write(byteBuffer);
				byteBuffer.clear();
			}

			stopWatch.stop();
			float time = stopWatch.getTime() / 1000f;
			String size2 = FileUtils.byteCountToDisplaySize(size);
			float speed = size / time / 1024f / 1024f * 8; // MB/s * 8bit = Mbps

			LOGGER.info("전송시간: {} s, 크기: {}({}), 속도(Mbps): {} ", time, size, size2, speed);

		}
	}
}
