package kr.humanbase.socket;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoSocketServerMultiThread {

	int SERVER_PORT = 7777;

	public static void main(String[] args) throws IOException {
		EchoSocketServerMultiThread echoSocketServerMultiThread = new EchoSocketServerMultiThread();
		echoSocketServerMultiThread.start();
	}

	public static ExecutorService getCommonExecutorService(int nThreads) {
		return Executors.newFixedThreadPool(nThreads);
	}

	public void start() throws IOException {
		// 50개짜리 스레드 풀
		ExecutorService es = getCommonExecutorService(50);
		try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
//			 FileOutputStream fos = Utils.getCommonFileOutputStream()
		) {
//			Utils.serverTimeStamp("===============================", fos);
//			Utils.serverTimeStamp("Multi Thread Socket Echo Server 시작", fos);

			while (true) {
//				Utils.serverTimeStamp("---------------------------", fos);
//				Utils.serverTimeStamp("Echo Server 대기 중", fos);

				// accept() 는 연결 요청이 올 때까지 return 하지 않고 blocking

				// 연결 요청이 오면 새 thread 에서 요청 처리 로직 수행
				es.execute(() -> {
					try(Socket acceptedSocket = serverSocket.accept()) {
//						Utils.serverTimeStamp("Client 접속!!!", fos);
//						Utils.serverTimeStamp("Echo 시작", fos);
//                    Utils.sleep(500L);
						EchoProcessor.echo(acceptedSocket);
//						Utils.serverTimeStamp("Echo 완료", fos);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
			}
		}
	}
}