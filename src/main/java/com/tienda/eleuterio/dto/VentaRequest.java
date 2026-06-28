package com.tienda.eleuterio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * DTO para registrar una venta.
 * El frontend envía la lista de productos del carrito con sus cantidades.
 */
@Data
public class VentaRequest {

    @NotNull(message = "Los items de la venta son obligatorios")
    @Valid
    private List<ItemVentaRequest> items;

    /**
     * Un ítem del carrito de venta.
     */
    @Data
    public static class ItemVentaRequest {

        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;

        @NotNull @Positive(message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;
    }
}
