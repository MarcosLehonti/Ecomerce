package com.example.myapp.repository;
import com.example.myapp.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByUsuarioId(Long usuarioId);  // Para listar los productos de un usuario
}
