package com.tienda.eleuterio.security;

import com.tienda.eleuterio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de UserDetailsService para que Spring Security
 * pueda cargar los datos del usuario desde la base de datos PostgreSQL.
 *
 * Spring Security usa este servicio durante la autenticación para
 * verificar que el usuario existe y tiene los roles correctos.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Busca el usuario en la base de datos por su email.
     * Spring Security llama este método automáticamente durante la validación del token.
     *
     * @param email el email del usuario (username en Spring Security)
     * @return UserDetails con email, password hasheado y rol
     * @throws UsernameNotFoundException si no existe el usuario
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email
                ));

        // Convertimos el rol a GrantedAuthority (ej: "ADMIN" → "ROLE_ADMIN")
        var authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol());

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(authority)
        );
    }
}
