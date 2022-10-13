package kr.no1.zerocopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientMain.class);

	private static final boolean ZERO_COPY = true;

	public static void main(String[] arguments) {
		LOGGER.info("ClientMain start! is zeroCopy? " + ZERO_COPY);

//			String host = arguments[1].split(":")[0];
//			int port = Integer.parseInt(arguments[1].split(":")[1]);
//			String path = arguments[2];
//			boolean zeroCopy = Boolean.getBoolean(arguments[3]);
		String host = "127.0.0.1";
		int port = 8080;
//		String path = "C:\\Users\\khpark\\Downloads\\postgl\\암임상 라이브러리 테이블 생성 POST-GL SCRIPT.sql";
//		String path = "C:\\Users\\khpark\\Downloads\\eGovFrameDev-4.0.0-Win-64bit.exe";
		String path = "C:\\Users\\khpark\\Downloads\\eclipse-inst-jre-win64.exe";


		SenderUtil.send(host, port, path, ZERO_COPY);

		LOGGER.info("ClientMain end!");
	}

}
