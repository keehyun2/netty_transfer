package kr.no1.zerocopy.data;

import java.io.Serializable;

public record CompositeFile  (
		String sourceFile, // 원본 파일 전체 경로
		String destFile, // 목적지(서버) 폴더 전제 경로
		long size // 파일 크기
) implements Serializable {
}
