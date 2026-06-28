package com.tienda.eleuterio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de respuesta del login exitoso.
 * El backend responde: { "token": "...", "nombre": "Don Eleuterio", "email": "..." }
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String nombre;
    private String email;
    private String rol;
}
