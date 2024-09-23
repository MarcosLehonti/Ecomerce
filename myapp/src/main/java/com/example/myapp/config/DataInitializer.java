package com.example.myapp.config;

// Ruta recomendada: src/main/java/com/example/myapp/config/DataInitializer.java

import com.example.myapp.model.Role;
import com.example.myapp.model.User;
import com.example.myapp.repository.RoleRepository;
import com.example.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Crear roles si no existen
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }

        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }

        // Crear el usuario por defecto "admin" si no existe
        Optional<User> adminUser = userRepository.findByUsername("admin");
        if (adminUser.isEmpty()) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));  // Contraseña por defecto, cámbiala si es necesario
            user.setFullName("Administrador del Sistema");
            user.setEmail("admin@example.com");

            // Asignar el rol "ROLE_ADMIN"
            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(adminRole);

            user.setRoles(roles);

            userRepository.save(user);

            System.out.println("Usuario admin creado con éxito");
        }
    }
}
