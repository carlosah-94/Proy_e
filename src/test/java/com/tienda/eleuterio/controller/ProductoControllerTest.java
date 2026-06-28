package com.tienda.eleuterio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.eleuterio.model.Categoria;
import com.tienda.eleuterio.model.Producto;
import com.tienda.eleuterio.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductoController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class
})
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto productoMock;

    @BeforeEach
    void setUp() {
        productoMock = new Producto();
        productoMock.setId(1L);
        productoMock.setNombre("Leche 1L");
        
        Categoria categoriaMock = new Categoria();
        categoriaMock.setId(1L);
        categoriaMock.setNombre("Lacteos");
        productoMock.setCategoria(categoriaMock);
        
        productoMock.setStockActual(10);
        productoMock.setPrecioUnitario(new BigDecimal("4.50"));
        productoMock.setFechaVencimiento(LocalDate.of(2025, 12, 31));
    }

    @Test
    void listarProductos_DeberiaRetornarListaYEstadoOk() throws Exception {
        when(productoService.obtenerTodosLosProductos()).thenReturn(Arrays.asList(productoMock));

        mockMvc.perform(get("/api/productos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Leche 1L"))
                .andExpect(jsonPath("$[0].categoria.nombre").value("Lacteos"));
    }

    @Test
    void agregarProducto_DeberiaRetornarProductoCreadoYEstadoCreated() throws Exception {
        when(productoService.guardarProducto(any(Producto.class))).thenReturn(productoMock);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Leche 1L"));
    }
}

