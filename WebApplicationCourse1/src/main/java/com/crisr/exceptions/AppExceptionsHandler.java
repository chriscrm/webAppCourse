package com.crisr.exceptions;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.crisr.model.response.CustomErrorMessage;

@ControllerAdvice
public class AppExceptionsHandler {

	@ExceptionHandler(value = { UserServiceException.class })
	public ResponseEntity<Object> handleUserServiceException(UserServiceException ex, WebRequest request) {
		
		//Custom error message for print timestamp
		CustomErrorMessage errorMessage = new CustomErrorMessage(new Date(), ex.getMessage());
		
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	//handler all other generic exceptions 
	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> handleOtherExceptions(Exception ex, WebRequest request) {
		CustomErrorMessage errorMessage = new CustomErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
