package kr.no1.netty.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.IOException;

public class Client {
	public static void main(String[] args) throws InterruptedException, IOException {
		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
//							p.addLast(new ChunkedWriteHandler());
							p.addLast(
									new ObjectEncoder(),
									new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
							p.addLast(new ClientHandler());
						}
					});

			// Start the client.
			ChannelFuture f = b.connect("127.0.0.1", 8080).sync();
//			f.channel().write(new ChunkedFile(new File("C:\\Users\\khpark\\Downloads\\dbeaver-ce-22.1.5-x86_64-setup.exe")));
//			f.channel().writeAndFlush(new ChunkedFile(new File("C:\\Users\\khpark\\Downloads\\dbeaver-ce-22.1.5-x86_64-setup.exe")));

//			ChunkedFile cf = new ChunkedFile(new File("C:\\Users\\khpark\\Downloads\\dbeaver-ce-22.1.5-x86_64-setup.exe"))
//			f.channel().writeAndFlush(new ChunkedFile(new File("C:\\Users\\khpark\\Downloads\\dbeaver-ce-22.1.5-x86_64-setup.exe")));

			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} finally {
			// Shut down the event loop to terminate all threads.
			group.shutdownGracefully();
		}
	}
}
