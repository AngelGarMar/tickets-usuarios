package com.tickets.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserLogin implements Serializable {
    @NotBlank(message = "El campo username no debe estar vacío.")
    private String username;
    @NotBlank(message = "El campo password no debe estar vacío.")
    private String password;
}
