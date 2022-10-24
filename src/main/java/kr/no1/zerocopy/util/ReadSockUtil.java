package kr.no1.zerocopy.util;

import kr.no1.zerocopy.ThrowingConsumer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ReadSockUtil {

	private ReadSockUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ReadSockUtil.class);

	private static final int CAPACITY = 1024 * 1024 * 2; // 2 MB

	public static <T> T readObj(SocketChannel socketChannel) throws IOException {
		T resultList = null;
		ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
		if (socketChannel.read(buf) > -1) {
			buf.flip();
			int size = buf.getInt();
			ByteBuffer buf2 = ByteBuffer.allocate(size);
			if (socketChannel.read(buf2) > -1) {
				buf2.flip();
				resultList = SerializationUtils.deserialize(buf2.array());
				LOGGER.info("Object : {}, 크기(byte) : {}", resultList, size);
			}
		}
		return resultList;
	}

	public static void readFileBuffer(SocketChannel socketChannel, ThrowingConsumer<ByteBuffer> consumer) throws IOException {

		ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
		if (socketChannel.read(buf) > -1) {
			buf.flip();
			long fileSize = buf.getLong();
			LOGGER.info("Start! File read, size : {} byte", fileSize);
			ByteBuffer buffer = ByteBuffer.allocateDirect(CAPACITY);
			while (socketChannel.read(buffer) > -1 && fileSize > 0) {
				buffer.flip();
				LOGGER.debug("remain size : {}, buffer {}", fileSize, buffer);
				consumer.accept(buffer);
				fileSize -= buffer.limit();
				buffer.clear();

//				if (fileSize < CAPACITY) {
//					buffer = ByteBuffer.allocateDirect(Math.toIntExact(fileSize));
//				}
			}
			LOGGER.info("End! File read");
		}
	}
}
