package com.cesde.parkingFlow.exception.custom;

@SuppressWarnings("serial")
public class RegistroInvalido extends RuntimeException {
	
	public RegistroInvalido(String mensaje){
		super(mensaje);
	}

}