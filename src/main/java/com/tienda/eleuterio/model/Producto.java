package com.tienda.eleuterio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa un producto del inventario de la tienda.
 * Campos basados en el formulario del proyecto Node.js original.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del producto (ej: "Inca Cola", "Yogurt") */
    @Column(nullable = false, length = 150)
    private String nombre;

    /** Tamaño o presentación (ej: "3 litros", "personal", "1kg") */
    @Column(length = 100)
    private String presentacion;

    /** Tipo opcional (ej: "retornable", "vidrio", "no retornable") */
    @Column(length = 100)
    private String tipo;

    /** Precio de venta unitario en Soles (S/.) */
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    /** Cantidad disponible en almacén */
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    /** Categoría del producto (relación con la tabla categorias) */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    /** Fecha de vencimiento del producto (lote actual) */
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    /** Stock restante del lote antiguo antes de actualizar a la nueva fecha de vencimiento */
    @Column(name = "stock_lote_antiguo")
    private Integer stockLoteAntiguo = 0;

    /** Próxima fecha de vencimiento que aplicará cuando stockLoteAntiguo llegue a 0 */
    @Column(name = "fecha_vencimiento_proxima")
    private LocalDate fechaVencimientoProxima;

    /** Fecha de registro en el sistema */
    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();
}
