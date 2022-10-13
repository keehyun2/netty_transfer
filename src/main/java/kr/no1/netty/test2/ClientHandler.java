package kr.no1.netty.test2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("ClientHandler channelActive");

		Path path = Paths.get("C:\\Users\\khpark\\Downloads\\postgl\\암임상 라이브러리 테이블 생성 POST-GL SCRIPT.sql");
//		Path path = Paths.get("C:\\Users\\khpark\\Downloads\\eGovFrameDev-4.0.0-Win-64bit.exe");
		FileMetaVO f = new FileMetaVO(path.getFileName().toString(), Files.size(path));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(f);
		oos.close();
		baos.close();
		byte[] wrt = baos.toByteArray();
		ByteBuf buf = Unpooled.buffer();
		buf.writeBytes(wrt);

		// Let object serialisation exceptions propagate.
		ctx.writeAndFlush(buf).addListener(FIRE_EXCEPTION_ON_FAILURE);

		RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
//		FileChannel channel = file.getChannel();
//
//		FileBufVO fb = new FileBufVO();

		//SSL not enabled-can use zero-copy file transfer.
		//2. Call raf.getChannel() to get a FileChannel.
		//3. Encapsulate FileChannel into a DefaultFileRegion
		ctx.writeAndFlush(new DefaultFileRegion(file.getChannel(), 0, file.length()));

//		ctx.writeAndFlush(fb)
//				.addListener(FIRE_EXCEPTION_ON_FAILURE)
//				.addListener(CLOSE);

	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
		System.out.println(o);
		// server 로 부터 남은 용량을 받아서 이어 보내기?

		//		ctx.write(buf);
//		ctx.write(new ChunkedFile());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println(">>> Echo Client Error!!!!");
		cause.printStackTrace();
		ctx.close();
	}
}
