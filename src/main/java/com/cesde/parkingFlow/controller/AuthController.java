package com.cesde.parkingFlow.controller;

import com.cesde.parkingFlow.dto.LoginRequestDTO;
import com.cesde.parkingFlow.dto.RefreshRequestDTO;
import com.cesde.parkingFlow.dto.RegisterRequestDTO;
import com.cesde.parkingFlow.dto.response.TokenResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cesde.parkingFlow.service.AuthService;
import com.cesde.parkingFlow.service.RefreshTokenService;


@RestController//Expone endpoints REST
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor//Genera constructor de dependencias inyectadas
public class AuthController {
    private final AuthService authService;//Servicio que maneja logica de autenticacion
    
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")//Endpoints POST /api/v1/
    public ResponseEntity<TokenResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }


    //Llama al servicio para autenticar usuario y devuelve un token
    @PostMapping("/login")//Endpoint POST /api/v1/auth/login
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    //Recibe refreshToken y devuelve un nuevo accessToken
    @PostMapping("/refresh")//Endpoint POST /api/v1/auth/refresh
    public ResponseEntity<TokenResponseDTO> refresh(@RequestBody RefreshRequestDTO refresh) {
    	TokenResponseDTO response = refreshTokenService.verifyAndRotate(refresh.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
