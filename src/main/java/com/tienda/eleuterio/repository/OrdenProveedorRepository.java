package com.tienda.eleuterio.repository;

import com.tienda.eleuterio.model.OrdenProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrdenProveedorRepository extends JpaRepository<OrdenProveedor, Long> {
    List<OrdenProveedor> findAllByOrderByFechaRecepcionDesc();

    /** Suma total gastada en proveedores (para reportes) */
    @Query("SELECT COALESCE(SUM(o.costoTotal), 0) FROM OrdenProveedor o")
    BigDecimal calcularGastoTotalProveedores();

    /** Gasto total en un periodo específico */
    @Query("SELECT COALESCE(SUM(o.costoTotal), 0) FROM OrdenProveedor o WHERE o.fechaRecepcion >= CAST(:inicio AS date) AND o.fechaRecepcion <= CAST(:fin AS date)")
    BigDecimal calcularGastosEnPeriodo(java.time.LocalDateTime inicio, java.time.LocalDateTime fin);
}
