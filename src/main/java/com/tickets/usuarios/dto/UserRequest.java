package com.tickets.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserRequest implements Serializable {
    @NotBlank(message = "El campo nombre no debe estar vacío.")
    @Size(min = 1, max = 20)
    private String nombre;
    @Size(min = 0, max = 15)
    private String apaterno;
    @Size(min = 0, max = 15)
    private String amaterno;
    @NotBlank(message = "El campo username no debe estar vacío.")
    @Size(min = 1, max = 15)
    private String username;
    @NotBlank(message = "El campo password no debe estar vacío.")
    @Size(min = 1, max = 60)
    private String password;
    private boolean admin;
}
