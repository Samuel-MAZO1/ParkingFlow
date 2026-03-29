package com.cesde.parkingFlow.exception.custom;

@SuppressWarnings("serial")
public class NotFound extends RuntimeException {
	
	public NotFound(String mensaje){
		super(mensaje);
	}

}