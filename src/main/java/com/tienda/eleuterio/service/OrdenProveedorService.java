package com.tienda.eleuterio.service;

import com.tienda.eleuterio.dto.OrdenProveedorRequest;
import com.tienda.eleuterio.model.DetalleOrdenProveedor;
import com.tienda.eleuterio.model.OrdenProveedor;
import com.tienda.eleuterio.model.Producto;
import com.tienda.eleuterio.model.Proveedor;
import com.tienda.eleuterio.repository.OrdenProveedorRepository;
import com.tienda.eleuterio.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para el registro de órdenes de proveedor.
 *
 * Cuando el proveedor entrega mercadería, Don Eleuterio registra la orden.
 * El sistema actualiza el stock de cada producto automáticamente.
 */
@Service
@RequiredArgsConstructor
public class OrdenProveedorService {

    private final OrdenProveedorRepository ordenProveedorRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorService proveedorService;

    public List<OrdenProveedor> obtenerTodas() {
        return ordenProveedorRepository.findAllByOrderByFechaRecepcionDesc();
    }

    /**
     * Registra una nueva orden de proveedor y actualiza el stock de los productos.
     * La anotación @Transactional garantiza que todo se guarde o nada (atomicidad).
     *
     * @param request DTO con nombre del proveedor, fecha y lista de productos recibidos
     * @return la orden guardada
     */
    @Transactional
    public OrdenProveedor registrarOrden(OrdenProveedorRequest request) {
        // 1. Obtener o crear el proveedor
        Proveedor proveedor = proveedorService.obtenerOCrearPorNombre(request.getNombreProveedor());

        // 2. Crear la orden cabecera
        OrdenProveedor orden = new OrdenProveedor();
        orden.setProveedor(proveedor);
        orden.setFechaRecepcion(request.getFechaRecepcion());

        // 3. Construir los detalles y calcular el costo total
        List<DetalleOrdenProveedor> detalles = new ArrayList<>();
        BigDecimal costoTotal = BigDecimal.ZERO;

        for (OrdenProveedorRequest.DetalleOrdenRequest detalleReq : request.getDetalles()) {
            // Buscar el producto
            Producto producto = productoRepository.findById(detalleReq.getProductoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Producto no encontrado con ID: " + detalleReq.getProductoId()
                    ));

            // Lógica FIFO de vencimientos
            if (producto.getStockActual() == 0 || producto.getFechaVencimiento() == null || producto.getFechaVencimiento().isBefore(java.time.LocalDate.now())) {
                producto.setFechaVencimiento(detalleReq.getFechaVencimientoLote());
                producto.setStockLoteAntiguo(0);
                producto.setFechaVencimientoProxima(null);
            } else if (!detalleReq.getFechaVencimientoLote().equals(producto.getFechaVencimiento())) {
                // Hay stock antiguo, lo guardamos para descontar primero
                producto.setStockLoteAntiguo(producto.getStockActual());
                producto.setFechaVencimientoProxima(detalleReq.getFechaVencimientoLote());
            }

            // Incrementar el stock del producto en la BD
            producto.setStockActual(producto.getStockActual() + detalleReq.getCantidadRecibida());
            productoRepository.save(producto);

            // Crear el detalle de la orden
            DetalleOrdenProveedor detalle = new DetalleOrdenProveedor();
            detalle.setOrden(orden);
            detalle.setProducto(producto);
            detalle.setCantidadRecibida(detalleReq.getCantidadRecibida());
            detalle.setCostoTotal(detalleReq.getCostoTotal());
            detalle.setFechaVencimientoLote(detalleReq.getFechaVencimientoLote());

            detalles.add(detalle);
            costoTotal = costoTotal.add(detalleReq.getCostoTotal());
        }

        orden.setDetalles(detalles);
        orden.setCostoTotal(costoTotal);

        return ordenProveedorRepository.save(orden);
    }
}
