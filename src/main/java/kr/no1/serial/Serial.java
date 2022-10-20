package kr.no1.serial;

import io.netty.buffer.ByteBufUtil;
import kr.no1.netty.test2.FileMetaVO;
import org.apache.commons.lang3.SerializationUtils;

import java.util.HashMap;

public class Serial {
	public static void main(String[] args) {

		HashMap<String, String> foodType = new HashMap<>();

		foodType.put("Burger", "Fastfood");

		byte[] a = SerializationUtils.serialize(foodType);


		HashMap<String, String> foodType2 = SerializationUtils.deserialize(a);

		System.out.println(foodType2);
	}
}
