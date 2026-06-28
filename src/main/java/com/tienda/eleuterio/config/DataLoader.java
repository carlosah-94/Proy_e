package com.tienda.eleuterio.config;

import com.tienda.eleuterio.model.Categoria;
import com.tienda.eleuterio.model.Producto;
import com.tienda.eleuterio.model.Usuario;
import com.tienda.eleuterio.repository.CategoriaRepository;
import com.tienda.eleuterio.repository.ProductoRepository;
import com.tienda.eleuterio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Carga datos iniciales en la base de datos si esta se encuentra vacía.
 * Registra al administrador "Don Eleuterio" y productos de ejemplo.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Cargar Usuario Administrador (Don Eleuterio)
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Don Eleuterio");
            admin.setEmail("admin@eleuterio.com");
            admin.setPassword(passwordEncoder.encode("admin")); // Contraseña por defecto
            admin.setRol("ADMIN");
            admin.setActivo(true);
            admin.setCreadoEn(LocalDateTime.now());
            usuarioRepository.save(admin);
            System.out.println(">>> Usuario administrador (admin@eleuterio.com / admin) creado exitosamente.");
        }

        // 2. Cargar Categorías
        Map<String, Categoria> categoriasMap = new HashMap<>();
        if (categoriaRepository.count() == 0) {
            String[] nombresCategorias = {"Bebidas", "Bebidas alcoholicas", "Lácteos", "Abarrotes"};
            for (String nombre : nombresCategorias) {
                Categoria cat = new Categoria();
                cat.setNombre(nombre);
                categoriasMap.put(nombre, categoriaRepository.save(cat));
            }
            System.out.println(">>> Categorías iniciales creadas exitosamente.");
        } else {
            categoriaRepository.findAll().forEach(cat -> categoriasMap.put(cat.getNombre(), cat));
        }

        // 3. Cargar Productos de Muestra (del frontend Node.js)
        if (productoRepository.count() == 0) {
            Categoria catBebidas = categoriasMap.get("Bebidas");
            Categoria catAlcohol = categoriasMap.get("Bebidas alcoholicas");
            Categoria catLacteos = categoriasMap.get("Lácteos");
            Categoria catAbarrotes = categoriasMap.get("Abarrotes");

            // Coca cola 3L No retornable
            Producto p1 = new Producto();
            p1.setNombre("Coca cola");
            p1.setPresentacion("3 Litros");
            p1.setTipo("No retornable");
            p1.setPrecioUnitario(new BigDecimal("12.50"));
            p1.setStockActual(0);
            p1.setCategoria(catBebidas != null ? catBebidas : catAbarrotes);
            p1.setFechaVencimiento(LocalDate.of(2026, 11, 15));
            p1.setCreadoEn(LocalDateTime.now());
            productoRepository.save(p1);

            System.out.println(">>> Producto inicial de muestra creado exitosamente.");
        }
    }
}
