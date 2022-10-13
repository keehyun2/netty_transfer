package kr.no1.netty.test;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("ClientHandler channelActive");

//		FileUploadFile f = new FileUploadFile();
//		f.setFile_md5("zxczxc");
//		ChannelFuture future = ctx.writeAndFlush(f);
//		future.addListener(FIRE_EXCEPTION_ON_FAILURE); // Let object serialisation exceptions propagate.

//		ByteBuf buf = Unpooled.buffer();
//		buf.writeByte(0);
//		buf.writeInt(4); // 타이틀 길이
//		buf.writeCharSequence("한글xx", CharsetUtil.UTF_8);
//		CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
//		messageBuf.addComponents(buf);
//		ChannelFuture future = ctx.writeAndFlush(messageBuf); // 전송
//		future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
//		future.addListener(ChannelFutureListener.CLOSE);
//		future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
		System.out.println(o);
		// server 로 부터 남은 용량을 받아서 이어 보내기?

		//		ctx.write(buf);
//		ctx.write(new ChunkedFile(new File("C:\\Users\\khpark\\Desktop\\메모.txt")));

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println(">>> Echo Client Error!!!!");
		cause.printStackTrace();
		ctx.close();
	}
}
