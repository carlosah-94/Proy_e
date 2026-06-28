package com.tienda.eleuterio.repository;

import com.tienda.eleuterio.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Búsqueda por nombre (contiene, ignorando mayúsculas) */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /** Productos con stock menor o igual a un umbral (stock crítico) */
    List<Producto> findByStockActualLessThanEqual(Integer stockUmbral);

    /** Cuenta cuántos productos tienen stock bajo */
    long countByStockActualLessThanEqual(Integer stockUmbral);

    /** Productos que vencen antes de una fecha */
    List<Producto> findByFechaVencimientoBefore(LocalDate fecha);

    /** Total de productos en inventario */
    @Query("SELECT COUNT(p) FROM Producto p")
    long contarTotalProductos();
}
