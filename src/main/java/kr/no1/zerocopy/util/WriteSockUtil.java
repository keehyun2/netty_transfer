package kr.no1.zerocopy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;

public class WriteSockUtil {

	private WriteSockUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(WriteSockUtil.class);

	public static ByteBuffer convertToBuffer(byte[] byteArr) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + byteArr.length); // ByteBuffer 생성
		byteBuffer.putInt(byteArr.length); // write in buffer
		byteBuffer.put(byteArr); // write in buffer
		byteBuffer.flip();
		return byteBuffer;
	}

	public static long sendFile(SocketChannel socketChannel, Path path) {
		long writeBytes = 0;
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r");
			 FileChannel fileChannel = randomAccessFile.getChannel()
		) {
			long fileSize = fileChannel.size();
			ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
			byteBuffer.putLong(fileSize);
			byteBuffer.flip();
			socketChannel.write(byteBuffer);
			// file buffer
			LOGGER.info("fileChannel : {}, position: {}, size : {}", fileChannel, fileChannel.position(), fileSize);
			long position = 0;
			while (fileSize > 0) { // we still have bytes to transfer
				long transferBytes = fileChannel.transferTo(position, fileSize, socketChannel);
				if (transferBytes > 0) {
					LOGGER.debug("transferBytes : {}", transferBytes);
					position += transferBytes; // seeking position to last byte transferred
					fileSize -= transferBytes;
					writeBytes += transferBytes;
				}
			}
			LOGGER.info("FILE size(BYTE) : {}", fileChannel.size());
		} catch (FileNotFoundException e) {
			LOGGER.error("ERROR FileNotFoundException", e);
		} catch (IOException e) {
			LOGGER.error("ERROR IOException", e);
		}
		return writeBytes;
	}
}
