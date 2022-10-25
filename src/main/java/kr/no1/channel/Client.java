package kr.no1.channel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.LongStream;

public class Client {

	private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
	private static final int CAPACITY = 1024 * 1024 * 1; // 1 MB
	private static final long size = 1073741824L; // 1 GB

	public static void main(String[] args) throws IOException {
		LOGGER.info("Client start!");

		try (SocketChannel socketChannel = SocketChannel.open()) {
//			socketChannel.connect(new InetSocketAddress("127.0.0.1", 20000));
			socketChannel.connect(new InetSocketAddress("172.24.180.229", 20000));
			socketChannel.configureBlocking(true);

			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			Path path = Paths.get("C:\\Users\\khpark\\Desktop\\업무파일\\소스\\dummyfile-1GB(8Gbit).temp");
			try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r");
				 FileChannel fileChannel = randomAccessFile.getChannel()
			) {
				long fileSize = fileChannel.size();
				long position = 0;
				while (fileSize > position) { // we still have bytes to transfer
					position += fileChannel.transferTo(position, fileSize, socketChannel);
				}
			} catch (IOException e) {
				LOGGER.error("ERROR IOException", e);
			}

//			long sendSize = 0;
//			ByteBuffer byteBuffer = ByteBuffer.allocate(CAPACITY);
//			while(size > sendSize){
//				int i = 0;
//				while(CAPACITY > i){
//					byteBuffer.put((byte) (i/128));
//					i++;
//				}
//				byteBuffer.flip();
//				sendSize += socketChannel.write(byteBuffer);
//				byteBuffer.clear();
//			}

			stopWatch.stop();
			float time = stopWatch.getTime() / 1000f;
			String size2 = FileUtils.byteCountToDisplaySize(size);
			float speed = size / time / 1024f / 1024f * 8; // MB/s * 8bit = Mbps

			LOGGER.info("전송시간: {} s, 크기: {}({}), 속도(Mbps): {} ", time, size, size2, speed);

		}

	}
}
