// src/main/java/com/example/myapp/repository/RoleRepository.java

package com.example.myapp.repository;

import com.example.myapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
