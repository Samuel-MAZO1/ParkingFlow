package com.cesde.parkingFlow.service;

import com.cesde.parkingFlow.dto.LoginRequestDTO;
import com.cesde.parkingFlow.dto.RegisterRequestDTO;
import com.cesde.parkingFlow.dto.response.TokenResponseDTO;
import com.cesde.parkingFlow.entity.User;
import com.cesde.parkingFlow.enums.Rol;
import com.cesde.parkingFlow.exception.custom.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cesde.parkingFlow.repository.UserRepository;



@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    //Registra un nuevo usuario en el sistema
    public TokenResponseDTO register(RegisterRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RegistroInvalido("Email ya registrado");
        }
        
        if (usuarioRepository.existsByDocument(request.getDocument())) {
        	throw new RegistroInvalido("Documento ya registrado");
        }
        
        if (usuarioRepository.existsByPhone(request.getPhone())) {
        	throw new RegistroInvalido("Telefono ya registrado");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .document(request.getDocument())
                .phone(request.getPhone())
                .name(request.getName())
                .lastName(request.getLastName()) 
                .rol(Rol.ABONADO) // rol por defecto
                .activo(true)
                .build();

        usuarioRepository.save(user);

        return new TokenResponseDTO(
                jwtService.generateToken(user),
                jwtService.generateRefreshToken(user)
        );
    }

    //Inicia sesion y devuelve tokens
    public TokenResponseDTO login(LoginRequestDTO request) {
        User user = findByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Unauthorized("Credenciales inválidas");
        }

        String refreshToken = refreshTokenService.createRefreshToken(user);
        
        return new TokenResponseDTO(
                jwtService.generateToken(user),
                refreshToken 
        );
    }

  
    
    public User findByEmail(String email) {
    	User user = usuarioRepository.findByEmail(email).
    			orElseThrow(() -> new NotFound("Usuario no encontrado"));
    	
    	return user;
    	
    }
}