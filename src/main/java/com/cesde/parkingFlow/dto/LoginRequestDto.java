package com.cesde.parkingFlow.dto;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Para iniciar sesion(Credenciales del usuario)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
	
	@Email
    private String email;
    private String password;
}
