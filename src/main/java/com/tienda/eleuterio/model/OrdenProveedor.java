package com.tienda.eleuterio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cabecera de una orden de proveedor.
 * Representa la recepción de productos de un proveedor en una fecha determinada.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ordenes_proveedor")
public class OrdenProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDate fechaRecepcion;

    /** Costo total de todos los productos recibidos en esta orden */
    @Column(name = "costo_total", precision = 10, scale = 2)
    private BigDecimal costoTotal = BigDecimal.ZERO;

    /** Detalle de los productos recibidos en esta orden */
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleOrdenProveedor> detalles = new ArrayList<>();

    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();
}
