package com.tienda.eleuterio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Detalle de cada producto recibido en una orden de proveedor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_orden_proveedor")
public class DetalleOrdenProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Referencia a la orden cabecera */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenProveedor orden;

    /** Producto recibido */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /** Cantidad de unidades recibidas */
    @Column(name = "cantidad_recibida", nullable = false)
    private Integer cantidadRecibida;

    /** Costo total pagado por este lote de productos */
    @Column(name = "costo_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoTotal;

    /** Fecha de vencimiento de este lote específico */
    @Column(name = "fecha_vencimiento_lote")
    private java.time.LocalDate fechaVencimientoLote;
}
