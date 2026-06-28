package com.tienda.eleuterio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para registrar una nueva orden de proveedor.
 * Incluye la cabecera (nombre proveedor, fecha) y la lista de productos recibidos.
 */
@Data
public class OrdenProveedorRequest {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String nombreProveedor;

    @NotNull(message = "La fecha de recepción es obligatoria")
    private LocalDate fechaRecepcion;

    @NotNull(message = "Debe incluir al menos un producto")
    @Valid
    private List<DetalleOrdenRequest> detalles;

    /**
     * Representa un producto dentro de la orden.
     */
    @Data
    public static class DetalleOrdenRequest {

        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;

        @NotNull @Positive(message = "La cantidad debe ser mayor a 0")
        private Integer cantidadRecibida;

        @NotNull @Positive(message = "El costo total debe ser mayor a 0")
        private BigDecimal costoTotal;

        @NotNull(message = "La fecha de vencimiento del lote es obligatoria")
        private LocalDate fechaVencimientoLote;
    }
}
