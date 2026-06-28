package com.tienda.eleuterio.repository;

import com.tienda.eleuterio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /** Busca usuario por email para el proceso de login */
    Optional<Usuario> findByEmail(String email);

    /** Verifica si ya existe un usuario con ese email */
    boolean existsByEmail(String email);
}
