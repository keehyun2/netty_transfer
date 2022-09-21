package kr.humanbase.socket;

import java.io.*;
import java.net.Socket;

class EchoProcessor {

//	EchoProcessor(){
//
//	}

//	private static final FileOutputStream fos = Utils.getCommonFileOutputStream();

	public static void echo(Socket socket) throws IOException {

		try (final DataInputStream dis =new DataInputStream(socket.getInputStream());){
			System.out.println("readUTF : " + dis.readUTF());
		}

//		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//			 PrintWriter out = new PrintWriter(socket.getOutputStream())
//		) {
//			String clientMessage = in.readLine();  // in에 읽을 게 들어올 때까지 blocking
//			String serverMessage = "Server Echo - " + clientMessage + System.lineSeparator();
////			Utils.sleep(500L);  // Echo 처리에 0.5초가 걸리도록 주석 해제
//			out.println(serverMessage);
//			out.flush();
//		}
	}
}
