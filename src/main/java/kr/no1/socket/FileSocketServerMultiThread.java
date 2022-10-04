package kr.humanbase.socket;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileSocketServerMultiThread {

	int SERVER_PORT = 7777;

	public static final int BUFSIZE = 10 * 1024 * 1024;

	public static void main(String[] args) throws IOException {
//		FileSocketServerMultiThread echoSocketServerMultiThread = new FileSocketServerMultiThread();
//		echoSocketServerMultiThread.start();

		String outFile = "C:\\Users\\khpark\\Downloads\\test2\\test_123.exe";
		String splittedFileNameFormat = "C:\\Users\\khpark\\Downloads\\test2\\split_file_%d";
		int fileCount = 120;

		mergeFiles(outFile, splittedFileNameFormat, fileCount);

	}

	public void start() throws IOException {
		// 50개짜리 스레드 풀
		ExecutorService es = Executors.newFixedThreadPool(50);

		try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
		) {
			while (true) {
				// accept() 는 연결 요청이 올 때까지 return 하지 않고 blocking
				es.execute(() -> {
					try(Socket socket = serverSocket.accept()) {
						StopWatch stopWatch = new StopWatch();
						stopWatch.start();
						try (final DataInputStream dis =new DataInputStream(socket.getInputStream())){
							String fileName = dis.readUTF();
							System.out.println("fileName : " + fileName);
							File file = new File("C:\\Users\\khpark\\Downloads\\test2\\" + fileName);
							FileUtils.copyInputStreamToFile(dis, file);
						}
						stopWatch.stop();
						System.out.println("수행시간: " + stopWatch.getTime() + " ms");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
			}
		}
	}

	public static void mergeFiles(String outFile, String splittedFileNameFormat, int fileCount) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		System.out.println("Merge " + fileCount + " files into " + outFile);
		try (final FileChannel outChannel = new FileOutputStream(outFile).getChannel()) {
			for (int i = 0; i < fileCount; i++) {
				try(final FileChannel fileChannel = new FileInputStream(String.format(splittedFileNameFormat, i + 1)).getChannel()) {
					ByteBuffer bb = ByteBuffer.allocateDirect(BUFSIZE);
					while (fileChannel.read(bb) != -1) {
						bb.flip();
						outChannel.write(bb);
						bb.clear();
					}
				}
			}
			System.out.println("Merged!! ");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		stopWatch.stop();
		System.out.println("수행시간: " + stopWatch.getTime() + " ms");
	}
}