package com.tienda.eleuterio.controller;

import com.tienda.eleuterio.model.Producto;
import com.tienda.eleuterio.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para la gestión del inventario de productos de Eleuterio")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Obtener todos los productos", description = "Retorna una lista completa de todos los productos en el inventario.")
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener producto por ID", description = "Obtiene los detalles de un producto específico.")
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar productos por nombre", description = "Retorna una lista de productos cuyo nombre coincida parcialmente.")
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String nombre) {
        List<Producto> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener productos con stock crítico", description = "Retorna los productos cuyo stock es menor o igual al umbral especificado.")
    @GetMapping("/bajo-stock")
    public ResponseEntity<List<Producto>> obtenerBajoStock(@RequestParam(defaultValue = "10") int umbral) {
        List<Producto> productos = productoService.obtenerProductosStockCritico(umbral);
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Agregar nuevo producto", description = "Guarda un producto nuevo en la base de datos.")
    @PostMapping
    public ResponseEntity<Producto> agregarProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.guardarProducto(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar producto existente", description = "Actualiza los datos de un producto por su ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            Producto actualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener imagen de producto", description = "Busca y devuelve la imagen del producto normalizando su nombre.")
    @GetMapping(value = "/imagen/{nombre}", produces = {org.springframework.http.MediaType.IMAGE_JPEG_VALUE, org.springframework.http.MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable String nombre) {
        try {
            java.nio.file.Path dir = java.nio.file.Paths.get("imagenes_productos");
            if (!java.nio.file.Files.exists(dir)) {
                return ResponseEntity.notFound().build();
            }
            
            String normalizedNombre = nombre.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            
            try (java.util.stream.Stream<java.nio.file.Path> stream = java.nio.file.Files.list(dir)) {
                java.util.Optional<java.nio.file.Path> imagePath = stream.filter(path -> {
                    String fileName = path.getFileName().toString();
                    String nameWithoutExt = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                    String normalizedFileName = nameWithoutExt.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    return normalizedFileName.equals(normalizedNombre);
                }).findFirst();
                
                if (imagePath.isPresent()) {
                    byte[] imageBytes = java.nio.file.Files.readAllBytes(imagePath.get());
                    String fileName = imagePath.get().getFileName().toString().toLowerCase();
                    org.springframework.http.MediaType mediaType = fileName.endsWith(".png") ? org.springframework.http.MediaType.IMAGE_PNG : org.springframework.http.MediaType.IMAGE_JPEG;
                    return ResponseEntity.ok().contentType(mediaType).body(imageBytes);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
