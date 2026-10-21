package com.tickets.usuarios.dto;

import com.tickets.usuarios.entities.Role;
import lombok.Data;

import java.io.Serializable;

@Data
public class RoleResponse implements Serializable {
    private int id;
    private String role;

    public RoleResponse(Role role) {
        this.id = role.getId();
        this.role = role.getRole();
    }
}
