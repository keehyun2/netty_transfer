package kr.no1.zerocopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ServerMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);
	private static final int PORT = 8080;
	private static final int N_THREAD = 8;
	private static final String COMPOSITE_FILE = "COMPOSITE_FILE";
	private static final String STRING = "STRING";

	public static void main(String[] arguments) throws IOException {
		LOGGER.info("ServerMain start!");
		ServerChannel sc = new ServerChannel(PORT, N_THREAD);
		sc.openSocketChannel(socketChannel -> {
			List<String> dataTypeList = sc.readList(socketChannel);
			for (String type : dataTypeList) {
				switch (type) {
					case COMPOSITE_FILE -> sc.readCompositeFile(socketChannel);
					case STRING -> sc.readStringBuf(socketChannel);
					default -> throw new IllegalStateException("Unexpected value: " + type);
				}
			}
			socketChannel.close(); //
		});
	}

}
