package kr.no1.zerocopy;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ServerChannel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannel.class);
	private static final StopWatch stopWatch = new StopWatch();
	private final int port;
	private final ExecutorService es;

	public ServerChannel(int port, int nThreads) {
		this.port = port;
		es = Executors.newFixedThreadPool(nThreads);
	}

	public void openSocketChannel(Consumer<SocketChannel> consumer) throws IOException, InterruptedException {
		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(true);
			while (true) {
//				try (SocketChannel socketChannel = serverSocketChannel.accept()) {
				SocketChannel socketChannel = serverSocketChannel.accept();
				es.execute(() -> {
//					LOGGER.info("Thread: {}", Thread.currentThread().getName());
//					stopWatch.start();
					consumer.accept(socketChannel);
					try {
						socketChannel.close();
					} catch (IOException e) {
						LOGGER.error("ERROR", e);
					}

//					stopWatch.stop();
//					LOGGER.info("실행시간 : {} milliseconds", stopWatch.getTime());
//					stopWatch.reset();
				});
//				} catch (IOException e) {
//					LOGGER.error("ERROR", e);
//				}

//				SocketChannel socketChannel = serverSocketChannel.accept();
//				CompletableFuture.runAsync(()->{
////					LOGGER.info("Thread: {}", Thread.currentThread().getName());
//					stopWatch.start();
////					LOGGER.info("Accepted : {}", socketChannel);
//
//					consumer.accept(socketChannel);
//
//					stopWatch.stop();
////					LOGGER.info("실행시간 : {} milliseconds", stopWatch.getTime());
//					stopWatch.reset();
//				}, es)
//				.thenRun(()-> {
//					try {
//						socketChannel.close();
//					} catch (IOException e) {
//						throw new RuntimeException(e);
//					}
//				});
			}
		}
	}

}
