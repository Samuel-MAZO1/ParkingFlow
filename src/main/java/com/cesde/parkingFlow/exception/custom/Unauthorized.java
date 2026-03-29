package com.cesde.parkingFlow.exception.custom;

@SuppressWarnings("serial")
public class Unauthorized extends RuntimeException{

	public Unauthorized(String message) {
	 super(message);	
	}
}