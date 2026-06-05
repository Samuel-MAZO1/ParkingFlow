package com.cesde.parkingFlow.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cesde.parkingFlow.exception.custom.Unauthorized;
import com.cesde.parkingFlow.dto.response.TokenResponseDTO;
import com.cesde.parkingFlow.entity.RefreshToken;
import com.cesde.parkingFlow.entity.User;
import com.cesde.parkingFlow.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
	
	private final JwtService jwtService;
	private final RefreshTokenRepository refreshTokenRepository;
	
	@Value("${jwt.refresh-expiration}")
	 private long jwtRefresh;
	
	
	@Transactional
public String createRefreshToken(User user) {
    	
		refreshTokenRepository.deleteByUsuario(user);
		refreshTokenRepository.flush();
		
        String refreshTokenJwt = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenJwt);
        refreshToken.setExpirationDate(
            Instant.now().plusMillis(jwtRefresh) 
        );

        refreshTokenRepository.save(refreshToken);
        
        return refreshTokenJwt;
    }

@Transactional
public TokenResponseDTO verifyAndRotate(String requestToken) {
    
    RefreshToken refreshToken = refreshTokenRepository.findByToken(requestToken)
            .orElseThrow(() -> new Unauthorized("Debe ingresar un refresh token valido"));

    
    if (refreshToken.isExpired()) {
        refreshTokenRepository.delete(refreshToken);
        throw new Unauthorized("El refresh token ha expirado. Por favor, inicie sesión de nuevo");
    }

    // 3. Obtener el usuario asociado
    User user = refreshToken.getUser();
   
    String newRefreshToken = createRefreshToken(user);
    String newAccessToken = jwtService.generateToken(user);

    return new TokenResponseDTO(newAccessToken, newRefreshToken);
}
}