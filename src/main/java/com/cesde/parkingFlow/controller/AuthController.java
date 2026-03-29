package com.cesde.parkingFlow.controller;

import com.cesde.parkingFlow.dto.LoginRequestDto;
import com.cesde.parkingFlow.dto.RefreshRequestDto;
import com.cesde.parkingFlow.dto.RegisterRequestDto;
import com.cesde.parkingFlow.dto.response.TokenResponseDto;

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
    public ResponseEntity<TokenResponseDto> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }


    //Llama al servicio para autenticar usuario y devuelve un token
    @PostMapping("/login")//Endpoint POST /api/v1/auth/login
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    //Recibe refreshToken y devuelve un nuevo accessToken
    @PostMapping("/refresh")//Endpoint POST /api/v1/auth/refresh
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshRequestDto refresh) {
    	TokenResponseDto response = refreshTokenService.verifyAndRotate(refresh.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
