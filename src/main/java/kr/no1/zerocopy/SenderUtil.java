package kr.no1.zerocopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SenderUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(SenderUtil.class);
	private static final int CAPACITY = 1024 * 1024 * 2; // 2 MB

	private static final boolean ZERO_COPY = true;

	public static void main(String[] arguments) {
		LOGGER.info("SenderUtil start! is zeroCopy? " + ZERO_COPY);

		String host = "127.0.0.1";
		int port = 8080;
//		String path = "C:\\Users\\khpark\\Downloads\\postgl\\암임상 라이브러리 테이블 생성 POST-GL SCRIPT.sql";
//		String path = "C:\\Users\\khpark\\Downloads\\eGovFrameDev-4.0.0-Win-64bit.exe";
		String path = "C:\\Users\\khpark\\Downloads\\eclipse-inst-jre-win64.exe";


		SenderUtil.fileSend(host, port, path, ZERO_COPY);

		LOGGER.info("SenderUtil end!");
	}

	public static void fileSend(String host, int port, String testFilePath, boolean zeroCopy) {

		try (SocketChannel socketChannel = SocketChannel.open()) {
			socketChannel.connect(new InetSocketAddress(host, port));
			socketChannel.configureBlocking(true);

			writeStringBuf(socketChannel, "한국박기현");

//			try (RandomAccessFile randomAccessFile = new RandomAccessFile(testFilePath, "r");
//				 FileChannel fileChannel = randomAccessFile.getChannel()
//			) {
//				long transferSize = transfer(socketChannel, fileChannel, zeroCopy);
//			}

		} catch (IOException e) {
			LOGGER.error("Send error", e);
		}
	}

	private static int writeStringBuf(SocketChannel socketChannel, String title) throws IOException {
		byte[] titleBytes = title.getBytes(StandardCharsets.UTF_8);	// string 을 byte[] 로 변환
		ByteBuffer stringBuf = ByteBuffer.allocate(4 + titleBytes.length); // ByteBuffer 생성
		stringBuf.putInt(titleBytes.length); // write in buffer
		stringBuf.put(titleBytes); // write in buffer
		stringBuf.flip();
		return socketChannel.write(stringBuf);
	}

	private static long transfer(SocketChannel socketChannel, FileChannel fileChannel, boolean zeroCopy) throws IOException {
		int transferSize = 0;

		if (zeroCopy) {
			// file buffer
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

		LOGGER.info("transferSize : {}", transferSize);
		return transferSize;
	}

}