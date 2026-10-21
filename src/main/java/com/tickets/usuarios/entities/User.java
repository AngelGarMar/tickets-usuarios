package com.tickets.usuarios.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank
    @Size(min = 1, max = 20)
    private String nombre;
    @Size(min = 0, max = 15)
    private String apaterno;
    @Size(min = 0, max = 15)
    private String amaterno;
    @NotBlank
    @Size(min = 1, max = 15)
    private String username;
    @NotBlank
    @Size(min = 1, max = 60)
    private String password;
    @NotNull
    private int estatus;
    private Timestamp creado;
    private Timestamp actualizado;
    @JsonIgnoreProperties({"users", "handler", "hibernateLazyInitializer"})
    @ManyToMany
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "usuarios_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = {"usuarios_id", "roles_id"})}
    )
    private List<Role> roles;

    public User() {
        roles = new ArrayList<>();
    }
}
