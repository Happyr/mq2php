package com.happyr.mq2php.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * Marshal and unmarshal JSON programmatically.
 *
 */
public class Marshaller {
	private static ObjectMapper objectMapper = new ObjectMapper();

	private Marshaller() {
	}

	public static <T> T valueOf(byte[] s, Class<T> clazz)
			throws IllegalArgumentException {
		try {
			return objectMapper.readValue(s, clazz);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static byte[] toBytes(Object jsonModel) {
		try {
			return objectMapper.writeValueAsBytes(jsonModel);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
}
