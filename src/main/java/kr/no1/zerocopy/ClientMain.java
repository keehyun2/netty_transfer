package kr.no1.zerocopy;

import kr.no1.zerocopy.data.CompositeFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ClientMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientMain.class);
	private static final int CAPACITY = 1024 * 1024 * 2; // 2 MB

	private static final String HOST = "127.0.0.1";
//	private static final String HOST = "172.24.180.229";
	private static final int PORT = 8080;

	public static void main(String[] arguments) {
		LOGGER.info("start!");

//		String path = "C:\\Users\\khpark\\Downloads\\postgl\\암임상 라이브러리 테이블 생성 POST-GL SCRIPT.sql";
//		String path = "C:\\Users\\khpark\\Downloads\\eGovFrameDev-4.0.0-Win-64bit.exe";
		String path = "C:\\Users\\khpark\\Downloads\\eclipse-inst-jre-win64.exe";

		Path smallFile = Paths.get("C:", "Users", "khpark", "Downloads", "postgl", "암임상 라이브러리 테이블 생성 POST-GL SCRIPT.sql");
		Path bigFile = Paths.get("C:\\Users\\khpark\\Downloads\\eGovFrameDev-4.0.0-Win-64bit.exe");

		List<Long> aList = LongStream.rangeClosed(1, 1).boxed().collect(Collectors.toList());

		aList.parallelStream().forEach(v -> {
			ClientChannel cc = new ClientChannel(HOST, PORT);
			String source = "C:\\Users\\khpark\\Desktop\\업무파일\\소스\\ideaIU-2022.2.1.exe";
//			String dest = "C:\\Users\\HB01\\Desktop\\목적지\\ideaIU-2022.2.1.exe";
			String dest = "C:\\Users\\khpark\\Desktop\\업무파일\\소스\\ideaIU-2022.2.1.exe";
			cc.write(new CompositeFile(source, dest, 1));
//			cc.write("한글");
//			cc.write(smallFile);
//			cc.write(bigFile);
			int sendSize = cc.flushBuffer();
			LOGGER.info("end! data size(BYTE): {}", sendSize);
		});
	}

}