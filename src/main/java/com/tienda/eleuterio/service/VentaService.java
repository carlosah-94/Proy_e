package com.tienda.eleuterio.service;

import com.tienda.eleuterio.dto.VentaRequest;
import com.tienda.eleuterio.model.DetalleVenta;
import com.tienda.eleuterio.model.Producto;
import com.tienda.eleuterio.model.Venta;
import com.tienda.eleuterio.repository.ProductoRepository;
import com.tienda.eleuterio.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para el registro de ventas.
 *
 * Cuando Don Eleuterio finaliza una venta, el sistema:
 * 1. Verifica que haya stock suficiente para cada producto
 * 2. Descuenta el stock del inventario
 * 3. Guarda la venta con todos sus detalles
 */
@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    /**
     * Registra una venta y descuenta el stock de los productos vendidos.
     * @Transactional garantiza que si algo falla (ej: stock insuficiente),
     * no se guarda nada en la base de datos (rollback automático).
     */
    @Transactional
    public Venta registrarVenta(VentaRequest request) {
        Venta venta = new Venta();
        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (VentaRequest.ItemVentaRequest item : request.getItems()) {
            // Buscar el producto
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Producto no encontrado con ID: " + item.getProductoId()
                    ));

            // Verificar stock disponible
            if (producto.getStockActual() < item.getCantidad()) {
                throw new RuntimeException(
                    "Stock insuficiente para '" + producto.getNombre() +
                    "'. Disponible: " + producto.getStockActual() +
                    ", solicitado: " + item.getCantidad()
                );
            }

            // Calcular subtotal de este producto
            BigDecimal subtotal = producto.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));

            // Descontar del inventario
            producto.setStockActual(producto.getStockActual() - item.getCantidad());
            
            // Lógica FIFO de vencimiento
            if (producto.getStockLoteAntiguo() != null && producto.getStockLoteAntiguo() > 0) {
                producto.setStockLoteAntiguo(producto.getStockLoteAntiguo() - item.getCantidad());
                if (producto.getStockLoteAntiguo() <= 0) {
                    // El lote antiguo se agotó, promovemos la nueva fecha de vencimiento
                    producto.setFechaVencimiento(producto.getFechaVencimientoProxima());
                    producto.setFechaVencimientoProxima(null);
                    producto.setStockLoteAntiguo(0);
                }
            }

            productoRepository.save(producto);

            // Crear el detalle de venta (guarda el precio histórico al momento de la venta)
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioUnitario());
            detalle.setSubtotal(subtotal);

            detalles.add(detalle);
            total = total.add(subtotal);
        }

        venta.setTotal(total);
        venta.setDetalles(detalles);

        return ventaRepository.save(venta);
    }
}
