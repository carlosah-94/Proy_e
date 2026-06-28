package com.tienda.eleuterio.controller;

import com.tienda.eleuterio.dto.OrdenProveedorRequest;
import com.tienda.eleuterio.model.OrdenProveedor;
import com.tienda.eleuterio.service.OrdenProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@Tag(name = "Órdenes de Proveedor", description = "API para registrar el ingreso de mercadería de proveedores")
public class OrdenProveedorController {

    private final OrdenProveedorService ordenProveedorService;

    @Operation(summary = "Obtener todas las órdenes", description = "Retorna el historial de compras/órdenes recibidas de los proveedores.")
    @GetMapping
    public ResponseEntity<List<OrdenProveedor>> listarOrdenes() {
        return ResponseEntity.ok(ordenProveedorService.obtenerTodas());
    }

    @Operation(summary = "Registrar ingreso de mercadería", description = "Registra una orden de proveedor, calculando el costo total y aumentando el stock de los productos involucrados.")
    @PostMapping
    public ResponseEntity<OrdenProveedor> registrarOrden(@Valid @RequestBody OrdenProveedorRequest request) {
        OrdenProveedor nuevaOrden = ordenProveedorService.registrarOrden(request);
        return new ResponseEntity<>(nuevaOrden, HttpStatus.CREATED);
    }
}
