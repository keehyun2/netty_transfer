package kr.no1.netty.test2;

import java.io.Serial;
import java.io.Serializable;

public class FileMetaVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 422512342123241L;
	private final String title;
	private final long size;

	public FileMetaVO(String title, long size) {
		this.title = title;
		this.size = size;
	}

	public String getTitle() {
		return title;
	}

	public long getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "FileSender{" +
				"title='" + title + '\'' +
				", size=" + size +
				'}';
	}
}
