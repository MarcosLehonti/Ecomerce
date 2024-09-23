//package com.example.myapp.controller;
////import com.example.myapp.model.Role;
////import com.example.myapp.model.User;
////import com.example.myapp.repository.RoleRepository;
////import com.example.myapp.repository.UserRepository;
////import org.springframework.http.ResponseEntity;
////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
////import org.springframework.web.bind.annotation.*;
////import org.springframework.security.core.Authentication;
//import com.example.myapp.model.Role;
//import com.example.myapp.model.User;
//import com.example.myapp.repository.RoleRepository;
//import com.example.myapp.repository.UserRepository;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final BCryptPasswordEncoder passwordEncoder;
//
//    public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//
//    @CrossOrigin(origins = "http://localhost:3000")
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody User user) {
//        try {
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//            Set<Role> roles = new HashSet<>();
//            roles.add(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found")));
//            user.setRoles(roles);
//
//            userRepository.save(user);
//
//            return ResponseEntity.ok("Usuario registrado exitosamente");
//        } catch (Exception e) {
//            // Loguear la excepción para depurar
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
//        }
//    }
//
//
//    @GetMapping
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//
//
//    @CrossOrigin(origins = "http://localhost:3000")
//    // Nuevo endpoint para obtener los datos del perfil del usuario autenticado
//    @GetMapping("/perfil")
//    public ResponseEntity<?> obtenerPerfil(Authentication authentication) {
//        // Obtener el nombre de usuario desde la autenticación
//        String username = authentication.getName();
//
//        // Buscar el usuario por su nombre de usuario
//        User usuario = userRepository.findByUsername(username).orElse(null);
//
//        if (usuario == null) {
//            return ResponseEntity.status(404).body("Usuario no encontrado");
//        }
//
//        // Preparar los datos del usuario para devolver
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("fullName", usuario.getFullName());
//        userData.put("email", usuario.getEmail());
//        // Añadir más datos si es necesario
//
//        return ResponseEntity.ok(userData);
//    }
//
//
//
//
//
//}

package com.example.myapp.controller;

import com.example.myapp.model.Role;
import com.example.myapp.model.User;
import com.example.myapp.repository.RoleRepository;
import com.example.myapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método para registrar usuario (sin cambios)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found")));
            user.setRoles(roles);
            userRepository.save(user);
            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Método para que el administrador vea todos los usuarios
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")  // Solo el admin puede acceder
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Obtener perfil del usuario autenticado
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(Authentication authentication) {
        String username = authentication.getName();
        User usuario = userRepository.findByUsername(username).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", usuario.getFullName());
        userData.put("email", usuario.getEmail());
        return ResponseEntity.ok(userData);
    }

    // Actualizar perfil del usuario autenticado
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(Authentication authentication, @RequestBody Map<String, String> updatedData) {
        String username = authentication.getName();
        User usuario = userRepository.findByUsername(username).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        usuario.setFullName(updatedData.get("fullName"));
        usuario.setEmail(updatedData.get("email"));
        userRepository.save(usuario);

        return ResponseEntity.ok("Perfil actualizado exitosamente");
    }

    // Cambiar la contraseña del usuario autenticado
    @PutMapping("/perfil/cambiar-contraseña")
    public ResponseEntity<?> cambiarContraseña(Authentication authentication, @RequestBody Map<String, String> passwordData) {
        String username = authentication.getName();
        User usuario = userRepository.findByUsername(username).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        // Verificar si la contraseña actual es correcta
        if (!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
            return ResponseEntity.status(400).body("La contraseña actual es incorrecta");
        }

        // Actualizar la nueva contraseña
        usuario.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(usuario);

        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }

    // Obtener todos los roles disponibles
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")  // Solo el admin puede acceder a los roles
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    // Asignar un rol a un usuario (reemplazando los roles existentes)
    @PutMapping("/asignar-rol")
    @PreAuthorize("hasRole('ADMIN')")  // Solo el admin puede asignar roles
    public ResponseEntity<?> asignarRol(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String roleName = request.get("roleName");

            // Buscar al usuario por su nombre de usuario
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Buscar el rol por su nombre
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            // Limpiar todos los roles existentes del usuario
            user.getRoles().clear();

            // Asignar el nuevo rol
            user.getRoles().add(role);

            // Guardar el usuario con el nuevo rol
            userRepository.save(user);

            return ResponseEntity.ok("Rol asignado exitosamente al usuario y los roles anteriores fueron reemplazados.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al asignar rol: " + e.getMessage());
        }
    }


    @PostConstruct
    public void init() {
        // Crear el rol "ROLE_ADMIN_DE_PRODUCTOS" si no existe
        if (roleRepository.findByName("ROLE_ADMIN_DE_PRODUCTOS").isEmpty()) {
            Role productAdminRole = new Role();
            productAdminRole.setName("ROLE_ADMIN_DE_PRODUCTOS");
            roleRepository.save(productAdminRole);
        }

        // Crear el usuario "admin_productos" si no existe
        Optional<User> adminUser = userRepository.findByUsername("admin_productos");
        if (adminUser.isEmpty()) {
            User user = new User();
            user.setUsername("admin_productos");
            user.setPassword(passwordEncoder.encode("adminProductos123"));  // Contraseña por defecto
            user.setFullName("Administrador de Productos");
            user.setEmail("admin_productos@example.com");

            // Asignar el rol "ROLE_ADMIN_DE_PRODUCTOS" al usuario
            Set<Role> roles = new HashSet<>();
            Role adminDeProductosRole = roleRepository.findByName("ROLE_ADMIN_DE_PRODUCTOS")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(adminDeProductosRole);
            user.setRoles(roles);

            userRepository.save(user);

            System.out.println("Usuario 'admin_productos' creado exitosamente");
        }
    }
}



