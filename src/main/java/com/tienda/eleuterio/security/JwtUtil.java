package com.tienda.eleuterio.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad para generar y validar tokens JWT.
 *
 * Un JWT tiene 3 partes: Header.Payload.Signature
 * - Header: algoritmo de firma (HS256)
 * - Payload: datos del usuario (claims): email, rol, fecha de expiración
 * - Signature: hash que garantiza la integridad del token
 */
@Component
public class JwtUtil {

    /** Clave secreta leída desde application.properties */
    @Value("${app.jwt.secret}")
    private String secretKey;

    /** Tiempo de expiración en ms (leído desde application.properties) */
    @Value("${app.jwt.expiration}")
    private long expirationMs;

    /**
     * Genera la clave criptográfica a partir de la clave secreta en Base64.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
            java.util.Base64.getEncoder().encodeToString(secretKey.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un nuevo token JWT para el usuario autenticado.
     * @param userDetails datos del usuario (Spring Security)
     * @return token JWT en formato String
     */
    public String generarToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())       // El "subject" es el email
                .issuedAt(new Date())                     // Fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // Fecha de expiración
                .signWith(getSigningKey())                 // Firma con la clave secreta
                .compact();
    }

    /**
     * Extrae el email (subject) del token.
     */
    public String extraerEmail(String token) {
        return parsearClaims(token).getSubject();
    }

    /**
     * Verifica si el token es válido (email correcto y no expirado).
     */
    public boolean esTokenValido(String token, UserDetails userDetails) {
        final String email = extraerEmail(token);
        return (email.equals(userDetails.getUsername()) && !estaExpirado(token));
    }

    /**
     * Verifica si el token ya expiró.
     */
    private boolean estaExpirado(String token) {
        return parsearClaims(token).getExpiration().before(new Date());
    }

    /**
     * Parsea y retorna el payload (claims) del token.
     * Lanza excepción si el token es inválido o fue manipulado.
     */
    private Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
