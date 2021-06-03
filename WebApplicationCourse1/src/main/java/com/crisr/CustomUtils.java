package com.crisr;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.crisr.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class CustomUtils {

	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	// private final int ITERATIONS = 1000;
	// private final int KEY_LENGTH = 256;

	public String generateUserId(int length) {
		return generateRandomString(length);
	}
	
	public String generateAddressId(int length) {
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

	/*
	 * decrypt token, and get content, then get expiration date
	 * compare two dates 
	 */
	public static boolean hasTokenExpired(String token) {
		
		Claims claims = Jwts.parser()
				.setSigningKey(SecurityConstants.getTokenSecret())
				.parseClaimsJws(token).getBody();
		
		Date tokenExpirationDate = claims.getExpiration();
		Date todayDate = new Date();
		
		return tokenExpirationDate.before(todayDate);
	}

	/*
	 * Generating token using publicUserId, setting expiration time and secret token phrase
	 */
	public static String generateEmailVerificationToken(String publicUserId) {
		
		String token = Jwts.builder()
				.setSubject(publicUserId)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
				.compact();
		
		return token;
	}

	
	/*
	 * Generating reset token using public userId, valid time token 1 hour
	 */
	public static String generatePasswordResetToken(String userId) {
		
		String token = Jwts.builder()
				.setSubject(userId)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.RESET_EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
				.compact();
		
		return token;
	}

}
