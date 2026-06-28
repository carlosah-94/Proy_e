package com.tienda.eleuterio.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que se ejecuta UNA VEZ por cada request HTTP.
 *
 * Flujo de verificación:
 * 1. Extrae el header "Authorization: Bearer <token>"
 * 2. Valida que el token no esté expirado y sea auténtico
 * 3. Si es válido, carga el usuario y lo registra en el SecurityContext
 * 4. Spring Security usa el SecurityContext para saber si el usuario está autenticado
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Leer el header Authorization
        final String authHeader = request.getHeader("Authorization");

        // Si no hay token o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (quitar el prefijo "Bearer ")
        final String jwtToken = authHeader.substring(7);

        try {
            // 3. Extraer el email del token
            final String email = jwtUtil.extraerEmail(jwtToken);

            // 4. Si hay email y no hay autenticación previa en este request
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Cargar el usuario desde la BD
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 5. Validar el token
                if (jwtUtil.esTokenValido(jwtToken, userDetails)) {

                    // 6. Crear la autenticación y registrarla en el SecurityContext
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Spring Security ahora sabe que el usuario está autenticado
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token inválido o expirado: simplemente no autenticar, Spring Security bloqueará el acceso
            logger.warn("Token JWT inválido: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
