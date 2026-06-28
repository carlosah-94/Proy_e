package com.tienda.eleuterio.service;

import com.tienda.eleuterio.model.Proveedor;
import com.tienda.eleuterio.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public List<Proveedor> obtenerTodos() {
        return proveedorRepository.findAll();
    }

    public Proveedor guardar(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    /**
     * Busca un proveedor por nombre o lo crea si no existe.
     * Útil al registrar una orden de proveedor nueva.
     */
    public Proveedor obtenerOCrearPorNombre(String nombre) {
        return proveedorRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> {
                    Proveedor nuevo = new Proveedor();
                    nuevo.setNombre(nombre);
                    return proveedorRepository.save(nuevo);
                });
    }

    public void eliminar(Long id) {
        proveedorRepository.deleteById(id);
    }
}
