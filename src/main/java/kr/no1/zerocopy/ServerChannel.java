package kr.no1.zerocopy;

import kr.no1.zerocopy.data.CompositeFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static kr.no1.zerocopy.util.ReadSockUtil.readFileBuffer;
import static kr.no1.zerocopy.util.ReadSockUtil.readObj;

public class ServerChannel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannel.class);
	private final int port;
	private final ExecutorService es;

	public ServerChannel(int port, int nThreads) {
		this.port = port;
		es = Executors.newFixedThreadPool(nThreads);
	}

	public void openSocketChannel(ThrowingConsumer<SocketChannel> consumer) throws IOException {
		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(true);
			while (true) {
				SocketChannel socketChannel = serverSocketChannel.accept(); // 새로운 연결이 들어올때까지 blocking 됨
				es.execute(() -> consumer.accept(socketChannel));
			}
		}
	}

	/**
	 * 버퍼에서 리스트 객체를 읽어들입니다.
	 */
	public <T> List<T> readList(SocketChannel socketChannel) throws IOException {
		List<T> resultList = null;
		ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
		if (socketChannel.read(buf) > -1) {
			buf.flip();
			int size = buf.getInt();
			ByteBuffer buf2 = ByteBuffer.allocate(size);
			if (socketChannel.read(buf2) > -1) {
				buf2.flip();
				resultList = SerializationUtils.deserialize(buf2.array());
				LOGGER.info("List<String> : {}, 크기(byte) : {}", resultList, size);
			}
		}
		return resultList;
	}

	public String readStringBuf(SocketChannel socketChannel) throws IOException {
		String result = "";
		ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
		if (socketChannel.read(buf) > -1) {
			buf.flip();
			int size = buf.getInt();
			ByteBuffer buf2 = ByteBuffer.allocate(size);
			if (socketChannel.read(buf2) > -1) {
				buf2.flip();
				result = new String(buf2.array(), StandardCharsets.UTF_8);
				LOGGER.info("String : {}, 크기(byte) : {}", result, size);
			}
		}
		return result;
	}

	public void readCompositeFile(SocketChannel socketChannel) throws IOException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		CompositeFile cf = readObj(socketChannel);
		Path path = Paths.get(cf.destFile() + ".tempFile");

		try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "rw");
			 FileChannel fileChannel = randomAccessFile.getChannel()
		) {
//			readFileBuffer(socketChannel, fileChannel::write);
			readFileBuffer(socketChannel, (buff) -> {
			});
		}
		stopWatch.stop();
		float time = stopWatch.getTime() / 1000f;
		long size = cf.size();
		String size2 = FileUtils.byteCountToDisplaySize(size);
		float speed = size / time / 1024f / 1024f / 1024f;

		LOGGER.info("파일 전송시간: {} s, 파일 크기: {}({}), 속도(GB/s): {} ", time, size, size2, speed);

	}

}
