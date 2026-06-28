package com.tienda.eleuterio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa al usuario dueño de la tienda.
 * Solo existe un usuario (Don Eleuterio). No hay registro público.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // La contraseña se almacena como hash BCrypt (nunca en texto plano)
    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String rol = "ADMIN";

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Column(name = "ultima_descarga_reporte_ventas")
    private LocalDateTime ultimaDescargaReporteVentas = LocalDateTime.now();

    @Column(name = "ultima_descarga_reporte_proveedores")
    private LocalDateTime ultimaDescargaReporteProveedores = LocalDateTime.now();
}
