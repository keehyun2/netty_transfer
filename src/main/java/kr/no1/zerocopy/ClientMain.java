package kr.no1.zerocopy;

import kr.no1.zerocopy.data.CompositeFile;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.LongStream;

public class ClientMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientMain.class);

//	private static final String HOST = "127.0.0.1";
	private static final String HOST = "172.24.180.229";
	private static final int PORT = 8080;

	public static void main(String[] arguments) {
		LOGGER.info("start!");

		List<Long> aList = LongStream.rangeClosed(1, 2).boxed().toList();

		aList.parallelStream().forEach(v -> {
			ClientChannel cc = new ClientChannel(HOST, PORT);
//			String source = "C:\\Users\\khpark\\Desktop\\업무파일\\소스\\0.메인게시판.png";
//			String dest = "C:\\Users\\khpark\\Desktop\\업무파일\\소스\\0.메인게시판.png";
			String source = "C:\\Users\\khpark\\Desktop\\업무파일\\소스\\dummyfile-5GB(40Gbit).temp";
//			String dest = "C:\\Users\\khpark\\Desktop\\업무파일\\목적지\\dummyfile-5GB(40Gbit).temp";
			String dest = "C:\\Users\\HB01\\Desktop\\목적지\\dummyfile.temp";
			try {
				long size = Files.size(Path.of(source));
				cc.write(new CompositeFile(source, dest, size));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
//			cc.write("한글");
//			cc.write(smallFile);
//			cc.write(bigFile);
			long sendSize = cc.flushBuffer();
			LOGGER.info("end! data size(BYTE): {}({})", sendSize, FileUtils.byteCountToDisplaySize(sendSize));
		});
	}

}