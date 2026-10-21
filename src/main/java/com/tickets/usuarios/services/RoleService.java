package com.tickets.usuarios.services;

import com.tickets.usuarios.dto.RoleRequest;
import com.tickets.usuarios.models.ResponseService;

public interface RoleService {
    ResponseService createRole(RoleRequest request);
}
