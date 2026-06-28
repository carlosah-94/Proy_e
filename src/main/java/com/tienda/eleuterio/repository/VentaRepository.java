package com.tienda.eleuterio.repository;

import com.tienda.eleuterio.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    /** Ventas realizadas en un rango de fechas */
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);

    /** Total vendido en un rango de fechas (para el dashboard: ventas de hoy) */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal calcularTotalVentasEnPeriodo(LocalDateTime inicio, LocalDateTime fin);

    /** Total vendido de todos los tiempos (para reportes) */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v")
    BigDecimal calcularTotalVentasGeneral();
}
