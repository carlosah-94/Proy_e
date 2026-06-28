package com.tienda.eleuterio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO con los datos consolidados para el Panel de Control (Dashboard).
 * Contiene métricas en tiempo real calculadas desde la base de datos.
 */
@Data
@AllArgsConstructor
public class DashboardDTO {

    /** Total de ventas del día de hoy */
    private BigDecimal ventasHoy;

    /** Número de productos con stock crítico (≤ 10 unidades) */
    private long productosStockCritico;

    /** Total de productos registrados en el inventario */
    private long totalProductos;

    /** Total acumulado de ventas de todos los tiempos */
    private BigDecimal totalVentasGeneral;

    /** Total gastado en órdenes de proveedores */
    private BigDecimal gastoTotalProveedores;
}
