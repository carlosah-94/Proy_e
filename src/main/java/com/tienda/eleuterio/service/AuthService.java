package com.tienda.eleuterio.service;

import com.tienda.eleuterio.dto.LoginRequest;
import com.tienda.eleuterio.dto.LoginResponse;
import com.tienda.eleuterio.repository.UsuarioRepository;
import com.tienda.eleuterio.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación.
 * Maneja el proceso de login: verifica credenciales y genera el token JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    /**
     * Autentica al usuario y retorna un token JWT junto con sus datos.
     *
     * @param request DTO con email y contraseña
     * @return LoginResponse con el token JWT y datos del usuario
     * @throws org.springframework.security.core.AuthenticationException si las credenciales son incorrectas
     */
    public LoginResponse login(LoginRequest request) {
        // Spring Security verifica email + password con BCrypt automáticamente
        // Si las credenciales son incorrectas, lanza BadCredentialsException
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // Credenciales correctas: cargar el usuario
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // Generar el token JWT
        String token = jwtUtil.generarToken(userDetails);

        // Obtener datos adicionales del usuario para la respuesta
        var usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();

        return new LoginResponse(token, usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }
}
