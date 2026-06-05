package com.cesde.parkingFlow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Para solicitar la renovacion de un access token
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequestDTO {
    //Refresh token valido que el cliente envia para obtener un nuevo access token
    private String refreshToken;
}
