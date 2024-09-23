package com.example.myapp.model;
// src/main/java/com/example/myapp/model/Role.java

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public String getName() {
        return name;
    }

    // Generar manualmente el setter si Lombok no lo genera
    public void setName(String name) {
        this.name = name;
    }
}

