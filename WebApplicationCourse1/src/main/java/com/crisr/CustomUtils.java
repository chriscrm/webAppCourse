package com.crisr;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class CustomUtils {

	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	// private final int ITERATIONS = 1000;
	// private final int KEY_LENGTH = 256;

	public String generateUserId(int length) {
		return generateRandomString(length);
	}

	/**
	 * Generate a random string for a given length
	 * @param length
	 * @return a random string or characters
	 */
	private String generateRandomString(int length) {

		StringBuilder returnValue = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}

		return new String(returnValue);
	}

}
