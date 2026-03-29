package com.cesde.parkingFlow.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.cesde.parkingFlow.service.JwtAuthenticationFilter;

import jakarta.servlet.DispatcherType;

//Config de seguridad, define endpoints publicos y cuales requieren autenticacion
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;//Inyecta filtro JWT personalizado

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;//Constructor que recibe el filtro JWT
    }

    //Rutas publicas de Swagger/OpenAPI
    private static final String[] SWAGGER_PATHS = {
            "/swagger",
            "/swagger/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/docs",
            "/docs/**",
            "/webjars/**",
            "/favicon.ico"
    };

    @Bean//Bean administrado por Spring
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))//Configura CORS
                .csrf(csrf -> csrf.disable())//Desactiva CSRF (No necesario en APIs REST)
                //Configura sesiones como STATELESS -> no se guarda estado en el servidor, se maneja con JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())//Desactiva login por formulario HTML
                .httpBasic(basic -> basic.disable())//Desactiva autenticacion basica

                .authorizeHttpRequests(auth -> auth
                        //Permite forwards internos y paginas de error
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        //Swagger libre (antes de las rutas de auth para prioridad)
                        .requestMatchers(SWAGGER_PATHS).permitAll()
                        //Permite acceso libre a endpoints de autenticacion
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        //Solo admin puede acceder a /admin/**
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/**").hasRole("ADMIN")
                        
                        .requestMatchers(HttpMethod.POST,"/api/v1/movimientos/**").hasRole("ADMIN")
                        
                        .requestMatchers("/api/v1/tarifas/**").hasRole("ADMIN")
                        //Lo demas requiere autenticacion
                        .anyRequest().authenticated()
                )

                //Manejo de errores de autenticacion/autorizacion con respuestas JSON
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setStatus(401);
                            response.getWriter().write(
                                    "{\"status\":401,\"error\":\"No autorizado\",\"message\":\""
                                            + authException.getMessage() + "\",\"path\":\""
                                            + request.getRequestURI() + "\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setStatus(403);
                            response.getWriter().write(
                                    "{\"status\":403,\"error\":\"Acceso denegado\",\"message\":\""
                                            + accessDeniedException.getMessage() + "\",\"path\":\""
                                            + request.getRequestURI() + "\"}"
                            );
                        })
                )

                //Inserta filtro JWT antes del filtro de autenticacion por user/password
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();//Devuelve la configuracion final
    }

    //Configuracion CORS para permitir requests del frontend y Swagger
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8082", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}