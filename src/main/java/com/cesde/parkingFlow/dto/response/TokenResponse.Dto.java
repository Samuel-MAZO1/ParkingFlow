package com.cesde.parkingFlow.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Para devolver tokens al cliente(Se usa en registro, login y refresh)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDto {
    private String accesToken;
    private String refreshToken;
}