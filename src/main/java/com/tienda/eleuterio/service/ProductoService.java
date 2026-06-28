package com.tienda.eleuterio.service;

import com.tienda.eleuterio.model.Producto;
import com.tienda.eleuterio.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión del inventario de productos.
 * Centraliza toda la lógica de negocio relacionada con productos.
 */
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    /** Obtiene todos los productos del inventario */
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    /** Busca un producto por su ID */
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    /** Busca productos por nombre (búsqueda parcial, sin importar mayúsculas) */
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /** Guarda un producto nuevo o actualiza uno existente */
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    /**
     * Actualiza un producto existente.
     * @throws RuntimeException si el producto no existe
     */
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        existente.setNombre(productoActualizado.getNombre());
        existente.setPresentacion(productoActualizado.getPresentacion());
        existente.setTipo(productoActualizado.getTipo());
        existente.setPrecioUnitario(productoActualizado.getPrecioUnitario());
        existente.setStockActual(productoActualizado.getStockActual());
        existente.setCategoria(productoActualizado.getCategoria());
        existente.setFechaVencimiento(productoActualizado.getFechaVencimiento());

        return productoRepository.save(existente);
    }

    /** Elimina un producto por su ID */
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    /**
     * Descuenta stock del producto.
     * @throws RuntimeException si no hay stock suficiente
     */
    public void descontarStock(Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

        if (producto.getStockActual() < cantidad) {
            throw new RuntimeException(
                "Stock insuficiente para '" + producto.getNombre() +
                "'. Disponible: " + producto.getStockActual() +
                ", solicitado: " + cantidad
            );
        }
        producto.setStockActual(producto.getStockActual() - cantidad);
        productoRepository.save(producto);
    }

    /** Incrementa el stock del producto (al recibir de proveedor) */
    public void incrementarStock(Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));
        producto.setStockActual(producto.getStockActual() + cantidad);
        productoRepository.save(producto);
    }

    /** Obtiene productos con stock crítico (≤ umbral) */
    public List<Producto> obtenerProductosStockCritico(int umbral) {
        return productoRepository.findByStockActualLessThanEqual(umbral);
    }
}
