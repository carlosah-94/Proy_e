package com.tienda.eleuterio.service;

import com.tienda.eleuterio.dto.DashboardDTO;
import com.tienda.eleuterio.repository.OrdenProveedorRepository;
import com.tienda.eleuterio.repository.ProductoRepository;
import com.tienda.eleuterio.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Servicio que calcula las métricas para el Panel de Control (Dashboard).
 * Consulta la base de datos y devuelve datos reales en tiempo real.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final OrdenProveedorRepository ordenProveedorRepository;

    /** Umbral de stock crítico: productos con 10 o menos unidades */
    private static final int UMBRAL_STOCK_CRITICO = 10;

    /**
     * Calcula y devuelve todas las métricas del dashboard.
     */
    public DashboardDTO obtenerMetricas() {
        // Ventas de hoy: desde las 00:00 hasta las 23:59 de hoy
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1).minusNanos(1);

        BigDecimal ventasHoy = ventaRepository
                .calcularTotalVentasEnPeriodo(inicioDia, finDia);

        long productosStockCritico = productoRepository
                .countByStockActualLessThanEqual(UMBRAL_STOCK_CRITICO);

        long totalProductos = productoRepository.count();

        BigDecimal totalVentasGeneral = ventaRepository.calcularTotalVentasGeneral();

        BigDecimal gastoTotalProveedores = ordenProveedorRepository
                .calcularGastoTotalProveedores();

        return new DashboardDTO(
                ventasHoy,
                productosStockCritico,
                totalProductos,
                totalVentasGeneral,
                gastoTotalProveedores
        );
    }
}
