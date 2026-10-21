package com.tickets.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleRequest {
    @NotBlank(message = "El campo role no debe estar vacío.")
    @Size(min = 1, max = 20)
    private String role;
}
