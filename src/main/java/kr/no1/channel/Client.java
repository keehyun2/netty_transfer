package kr.no1.channel;

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
			socketChannel.connect(new InetSocketAddress("172.24.180.229", 20000));
			socketChannel.configureBlocking(true);

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

		}
	}
}
