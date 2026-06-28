package com.tienda.eleuterio.controller;

import com.tienda.eleuterio.dto.DashboardDTO;
import com.tienda.eleuterio.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "API para obtener métricas generales del negocio en tiempo real")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Obtener métricas de negocio", description = "Calcula y retorna ventas diarias, productos con bajo stock, total de productos, y compras a proveedores.")
    @GetMapping
    public ResponseEntity<DashboardDTO> obtenerMetricas() {
        return ResponseEntity.ok(dashboardService.obtenerMetricas());
    }
}
