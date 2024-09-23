package com.example.myapp.controller;

import com.example.myapp.model.Producto;
import com.example.myapp.repository.ProductoRepository;
import com.example.myapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.nio.file.Path;
import java.nio.file.Paths;




@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final UserRepository userRepository;

    // Ruta para guardar las imágenes
    private final String rutaImagenes = "uploads/";

    public ProductoController(ProductoRepository productoRepository, UserRepository userRepository) {
        this.productoRepository = productoRepository;
        this.userRepository = userRepository;
    }

    // Listar todos los productos (acceso solo para admin_de_productos y usuarios)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN_DE_PRODUCTOS') or hasRole('ROLE_USER')")
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    // Cambiar el método para guardar solo el nombre del archivo en la base de datos
    @PostMapping("/crear-con-imagen")
    @PreAuthorize("hasRole('ADMIN_DE_PRODUCTOS') or hasRole('ADMIN')")
    public ResponseEntity<?> crearProductoConImagen(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("imagen") MultipartFile imagen) {

        try {
            // Guardar la imagen en el servidor
            String nombreArchivo = StringUtils.cleanPath(imagen.getOriginalFilename());
            Path rutaImagen = Paths.get(rutaImagenes + nombreArchivo);
            Files.copy(imagen.getInputStream(), rutaImagen, StandardCopyOption.REPLACE_EXISTING);

            // Crear y guardar el producto
            Producto nuevoProducto = new Producto();
            nuevoProducto.setNombre(nombre);
            nuevoProducto.setDescripcion(descripcion);
            nuevoProducto.setPrecio(precio);
            nuevoProducto.setImagenUrl("/uploads/" + nombreArchivo);  // Guardar solo la URL relativa de la imagen
            productoRepository.save(nuevoProducto);

            return ResponseEntity.ok("Producto creado exitosamente con imagen");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al subir la imagen: " + e.getMessage());
        }
    }


    // Método para actualizar un producto con imagen (acceso solo para admin_de_productos)
    @PutMapping("/{id}/actualizar-con-imagen")
    @PreAuthorize("hasRole('ADMIN_DE_PRODUCTOS')")
    public ResponseEntity<?> actualizarProductoConImagen(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("imagen") MultipartFile imagen) {

        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (productoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Producto no encontrado");
        }

        try {
            Producto producto = productoOpt.get();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);

            // Guardar la nueva imagen
            if (!imagen.isEmpty()) {
                String nombreArchivo = StringUtils.cleanPath(imagen.getOriginalFilename());
                Path rutaImagen = Paths.get(rutaImagenes + nombreArchivo);
                Files.copy(imagen.getInputStream(), rutaImagen, StandardCopyOption.REPLACE_EXISTING);
                producto.setImagenUrl(rutaImagen.toString());  // Actualizar la ruta de la imagen
            }

            productoRepository.save(producto);
            return ResponseEntity.ok("Producto actualizado exitosamente con imagen");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al subir la imagen: " + e.getMessage());
        }
    }

    // Eliminar un producto (acceso solo para admin_de_productos)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_DE_PRODUCTOS')")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return ResponseEntity.ok("Producto eliminado exitosamente");
    }


    // Método para servir imágenes desde la carpeta uploads
    @GetMapping("/imagen/{nombreArchivo}")
    public ResponseEntity<Resource> servirImagen(@PathVariable String nombreArchivo) {
        try {
            Path rutaImagen = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
            Resource recurso = new UrlResource(rutaImagen.toUri());

            if (recurso.exists() && recurso.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                        .body(recurso);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
