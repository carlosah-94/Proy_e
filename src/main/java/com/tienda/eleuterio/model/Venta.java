package com.tienda.eleuterio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cabecera de una venta realizada en la tienda.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Total cobrado al cliente */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    /** Fecha y hora exacta de la venta */
    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta = LocalDateTime.now();

    /** Detalle de los productos vendidos */
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detalles = new ArrayList<>();
}
