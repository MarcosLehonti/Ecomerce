package com.example.myapp.model;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.PropertyValues;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullName;

    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    // Agregar el getter para los roles
    public Set<Role> getRoles() {
        return roles;
    }

    // Setter para los roles (si es necesario)
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Getter para username
    public String getUsername() {
        return username;
    }

    // Setter para username (si es necesario)
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter para password
    public String getPassword() {
        return password;
    }

    // Setter para password (si es necesario)
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }


    // Generar manualmente los setters si Lombok no los genera
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}



