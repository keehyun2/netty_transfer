package kr.no1.netty.test2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.SerializationUtils;

import java.util.logging.Logger;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

	private static final Logger logger = Logger.getGlobal();

	private static final String folder1 = "C:\\Users\\khpark\\Desktop\\";

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("ServerHandler channelRead0: " + msg);

		ByteBuf buf = (ByteBuf) msg;

		try {
			byte[] byteArr = ByteBufUtil.getBytes(buf);
			FileMetaVO f = SerializationUtils.deserialize(byteArr);
			logger.info(f.getTitle());
		} catch (Exception e) {
			e.printStackTrace();
		}

//		FileMetaVO f = (FileMetaVO) ois.readObject();

//		if (ois.readObject() instanceof FileMetaVO fs) {
//			System.out.println("client receive : " + fs);
//			Path path = Paths.get(folder1 + fs.getTitle() + ".tempfile");
//			if(!Files.exists(path)) Files.createFile(path);
//		} else {
//
//		}

//		ByteBuffer byteBuffer = ((ByteBuf) msg).nioBuffer();

//		Path path = Paths.get(folder1 + "fs.getTitle()" + ".tempfile");
//		RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "rw");
//		FileChannel fileChannel = randomAccessFile.getChannel();
//
//		fileChannel.position(path.toFile().length());
//		fileChannel.write(byteBuffer);
//
//		fileChannel.close();
//		randomAccessFile.close();

//		try(BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))){
//			writer.
//		}catch(IOException ex){
//			ex.printStackTrace();
//		}

	}
}
