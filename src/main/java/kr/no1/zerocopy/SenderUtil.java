package kr.no1.zerocopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class SenderUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(SenderUtil.class);
	private static final int CAPACITY = 1024 * 1024 * 1; // 1 MB

	public static void send(String host, int port, String testFilePath, boolean zeroCopy) {

		try (SocketChannel socketChannel = SocketChannel.open();
			 RandomAccessFile randomAccessFile = new RandomAccessFile(testFilePath, "r");
			 FileChannel fileChannel = randomAccessFile.getChannel()
		) {
			socketChannel.connect(new InetSocketAddress(host, port));
			socketChannel.configureBlocking(true);

			long transferSize = transfer(socketChannel, fileChannel, zeroCopy);
			LOGGER.info("transferSize : {}", transferSize);
		} catch (IOException e) {
			LOGGER.error("Send error", e);
		}
	}

	private static long transfer(SocketChannel socketChannel, FileChannel fileChannel, boolean zeroCopy) throws IOException {
		int transferSize = 0;

		if (zeroCopy) {
			LOGGER.info("fileChannel : {}, position: {}, size : {}", fileChannel, fileChannel.position(), fileChannel.size());
			long size = fileChannel.size();
			int position = 0;
			while (size > 0) { // we still have bytes to transfer
				long writeBytes = fileChannel.transferTo(position, size, socketChannel);
				if (writeBytes > 0) {
					LOGGER.info("writeBytes : {}", writeBytes);
					position += writeBytes; // seeking position to last byte transferred
					size -= writeBytes; // {count} bytes have been transferred, remaining {size}
				}
				transferSize += Math.max(writeBytes, 0);
			}
		} else {
			ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);
			while (fileChannel.read(buffer) > 0) {
//				LOGGER.info(buffer.toString());
				buffer.flip();
				int writeBytes = socketChannel.write(buffer);
				buffer.clear();

				transferSize += Math.max(writeBytes, 0);
			}
		}

		return transferSize;
	}

}