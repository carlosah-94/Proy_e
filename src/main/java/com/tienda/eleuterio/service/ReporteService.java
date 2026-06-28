package com.tienda.eleuterio.service;

import com.tienda.eleuterio.model.Usuario;
import com.tienda.eleuterio.repository.OrdenProveedorRepository;
import com.tienda.eleuterio.repository.UsuarioRepository;
import com.tienda.eleuterio.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final VentaRepository ventaRepository;
    private final OrdenProveedorRepository ordenProveedorRepository;
    private final UsuarioRepository usuarioRepository;

    public BigDecimal obtenerTotalVentasSemana() {
        Usuario admin = usuarioRepository.findByEmail("admin@eleuterio.com")
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
        
        // Ventas desde la última descarga (reseteo)
        LocalDateTime inicio = admin.getUltimaDescargaReporteVentas();
        if (inicio == null) inicio = LocalDateTime.now().minusDays(7); // fallback
        
        return ventaRepository.calcularTotalVentasEnPeriodo(inicio, LocalDateTime.now());
    }

    public BigDecimal obtenerGastosProveedoresSemana() {
        Usuario admin = usuarioRepository.findByEmail("admin@eleuterio.com")
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
        
        // Gastos desde la última descarga (reseteo)
        LocalDateTime inicio = admin.getUltimaDescargaReporteProveedores();
        if (inicio == null) inicio = LocalDateTime.now().minusDays(7); // fallback
        
        return ordenProveedorRepository.calcularGastosEnPeriodo(inicio, LocalDateTime.now());
    }

    @Transactional
    public void resetearReporteVentas() {
        Usuario admin = usuarioRepository.findByEmail("admin@eleuterio.com")
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
        admin.setUltimaDescargaReporteVentas(LocalDateTime.now());
        usuarioRepository.save(admin);
    }

    @Transactional
    public void resetearReporteProveedores() {
        Usuario admin = usuarioRepository.findByEmail("admin@eleuterio.com")
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
        admin.setUltimaDescargaReporteProveedores(LocalDateTime.now());
        usuarioRepository.save(admin);
    }
}
