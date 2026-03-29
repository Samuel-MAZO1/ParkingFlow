package com.cesde.parkingFlow.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cesde.parkingFlow.exception.custom.*;
import com.cesde.parkingFlow.exception.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(exception = Unauthorized.class)
	public ResponseEntity<ErrorResponse> unauthorizedError(Unauthorized ex){
		ErrorResponse error = new ErrorResponse(
				HttpStatus.UNAUTHORIZED.value(),
				ex.getMessage(),
				LocalDateTime.now()
				);
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(exception = RegistroInvalido.class)
	public ResponseEntity<ErrorResponse> registroInvalido(RegistroInvalido ms){
		ErrorResponse error = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				ms.getMessage(),
				LocalDateTime.now()
				);
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);		
	}
	
	@ExceptionHandler(exception = NotFound.class)
	public ResponseEntity<ErrorResponse> notFound(NotFound ms){
		ErrorResponse error = new ErrorResponse(
				HttpStatus.NOT_FOUND.value(),
				ms.getMessage(),
				LocalDateTime.now()
				);
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);		
	}
	
	
	
	
	
}