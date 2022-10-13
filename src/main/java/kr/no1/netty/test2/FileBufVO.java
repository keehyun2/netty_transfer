package kr.no1.netty.test2;

import java.io.Serial;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class FileBufVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 422512342133241L;

	private ByteBuffer buf;
}
