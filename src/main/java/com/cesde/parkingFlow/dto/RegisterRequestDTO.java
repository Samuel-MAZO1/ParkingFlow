package com.cesde.parkingFlow.dto;

import com.cesde.parkingFlow.enums.Rol;
import lombok.Data;
import lombok.NoArgsConstructor;

//Registrar nuevo usuario
@NoArgsConstructor
@Data
public class RegisterRequestDTO {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String document;
    private String phone;
    private Rol rol;
}
