package com.tienda.eleuterio.controller;

import com.tienda.eleuterio.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "API para obtener métricas de reportes y reseteo de contadores")
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Obtener métricas de reportes", description = "Retorna el total de ventas y gastos en proveedores desde el último reseteo.")
    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> obtenerReportes() {
        Map<String, BigDecimal> reportes = new HashMap<>();
        reportes.put("ventas", reporteService.obtenerTotalVentasSemana());
        reportes.put("gastos", reporteService.obtenerGastosProveedoresSemana());
        return ResponseEntity.ok(reportes);
    }

    @Operation(summary = "Resetear contador de ventas", description = "Actualiza la fecha de última descarga para reiniciar el contador de ventas a 0.")
    @PostMapping("/reset-ventas")
    public ResponseEntity<Void> resetearVentas() {
        reporteService.resetearReporteVentas();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Resetear contador de gastos", description = "Actualiza la fecha de última descarga para reiniciar el contador de gastos a 0.")
    @PostMapping("/reset-gastos")
    public ResponseEntity<Void> resetearGastos() {
        reporteService.resetearReporteProveedores();
        return ResponseEntity.ok().build();
    }
}
