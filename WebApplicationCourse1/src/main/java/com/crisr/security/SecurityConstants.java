package com.crisr.security;

import com.crisr.SpringApplicationContext;

/**
 * 
 * @author crisr
 *
 */
public class SecurityConstants {

	public static final long EXPIRATION_TIME = 864000000; // 10 days, time in milliseconds
	public static final long RESET_EXPIRATION_TIME = 3600000; // 1 hour
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	public static final String VERIFICATION_URL = "/users/email-verification";

	public static String getTokenSecret() {
		AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
		return appProperties.getTokenSecret();
	}

}
