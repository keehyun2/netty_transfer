package kr.no1.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static java.lang.System.out;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		out.println("ServerHandler channelRead0: " + msg);
//		PooledUnsafeDirectByteBuf(ridx: 0, widx: 13, cap: 2048)

		ByteBuf byteBuf = (ByteBuf) msg;
		byte flag = byteBuf.readByte();
		if(flag == 0){
			int titleLength = byteBuf.readInt();
			out.println(titleLength);
			String s = byteBuf.readCharSequence(titleLength, CharsetUtil.UTF_8).toString();
			out.println(s);
//			ctx.writeAndFlush(Unpooled.);
		} else {
			byteBuf.resetReaderIndex();
			File file = new File("C:\\Users\\khpark\\Desktop\\메모2.exe.tempfile");//remember to change dest
			if (!file.exists()) {
				file.createNewFile();
			}

			ByteBuffer byteBuffer = byteBuf.nioBuffer();
			int titleLength = byteBuffer.getInt(); // 파일 제목 길이

			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
			FileChannel fileChannel = randomAccessFile.getChannel();

	//		while (byteBuffer.hasRemaining()){;
				fileChannel.position(file.length());
				fileChannel.write(byteBuffer);
	//		}

	//		byteBuf.release();  // SimpleChannelInboundHandler 는 자원을 자동으로 해제해서 필요없음.
			fileChannel.close();
			randomAccessFile.close();
		}

	}
}
