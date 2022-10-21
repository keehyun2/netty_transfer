package kr.no1.zerocopy;

import kr.no1.zerocopy.data.CompositeFile;
import org.apache.commons.lang3.SerializationUtils;
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

public class ServerChannel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannel.class);
	private static final int CAPACITY = 1024 * 1024 * 2; // 2 MB
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

	private <T> T readObj(SocketChannel socketChannel) throws IOException {
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

	public CompositeFile readCompositeFile(SocketChannel socketChannel) throws IOException {
		CompositeFile cf = readObj(socketChannel);
		Path path = Paths.get(cf.destFile());

		try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "rw");
			 FileChannel fileChannel = randomAccessFile.getChannel()
		) {
			readFileBuffer(socketChannel, fileChannel::write);
		}
		return cf;
	}

	private void readFileBuffer(SocketChannel socketChannel, ThrowingConsumer<ByteBuffer> consumer) throws IOException {
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

				if (fileSize < CAPACITY) {
					buffer = ByteBuffer.allocateDirect(Math.toIntExact(fileSize));
				}
			}
			LOGGER.info("End! File read");
		}
	}
}
