package com.cesde.parkingFlow.service;


import com.cesde.parkingFlow.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

//Generar y validad JWT
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    //Generar access token para usuario(incluye claims como email y roles)
    public String generateToken(User user) {
        String accessToken = Jwts.builder()
        		
        		    .header()   
    		        .add("typ", "JWT")
    		        .and()
    		        
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", List.of("ROLE_" + user.getRol().name()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), Jwts.SIG.HS256)
                .compact();
        
        return accessToken;
    }

    //Generar refresh token para un usuario
    public String generateRefreshToken(User user) {
        String refreshToken = Jwts.builder()
        		
        		    .header()   
        		    .add("typ", "JWT")
        		    .and()
        		
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), Jwts.SIG.HS256)
                .compact();
        
        return refreshToken;
    }
    

    //Validar token recibido en una peticion
    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

