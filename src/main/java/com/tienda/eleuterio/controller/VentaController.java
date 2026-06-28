package com.tienda.eleuterio.controller;

import com.tienda.eleuterio.dto.VentaRequest;
import com.tienda.eleuterio.model.Venta;
import com.tienda.eleuterio.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "API para registrar y listar ventas de la bodega de Don Eleuterio")
public class VentaController {

    private final VentaService ventaService;

    @Operation(summary = "Obtener todas las ventas", description = "Retorna el historial completo de ventas realizadas.")
    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return ResponseEntity.ok(ventaService.obtenerTodas());
    }

    @Operation(summary = "Registrar nueva venta", description = "Registra una venta, calculando subtotales y descontando stock de los productos vendidos.")
    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@Valid @RequestBody VentaRequest request) {
        Venta nuevaVenta = ventaService.registrarVenta(request);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }
}
