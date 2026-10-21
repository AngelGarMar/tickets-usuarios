package com.tickets.usuarios.services.impl;

import com.tickets.usuarios.constants.Constants;
import com.tickets.usuarios.dto.RoleRequest;
import com.tickets.usuarios.dto.RoleResponse;
import com.tickets.usuarios.entities.Role;
import com.tickets.usuarios.models.ResponseService;
import com.tickets.usuarios.repositories.RoleRepository;
import com.tickets.usuarios.services.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository repository;

    @Override
    @Transactional
    public ResponseService createRole(RoleRequest request) {
        // Se revisa si existe el mismo role
        log.info("revisando si ya existe el role");
        Optional<Role> roleOptional = repository.findByRole("ROLE_" + request.getRole().trim().toUpperCase());
        if (roleOptional.isPresent()) {
            log.info("ya existe el role");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.ROLE_EXISTS, null);
        }
        // Se guarda el rol
        log.info("guardando el role");
        Role role = Role.builder()
                .role("ROLE_" + request.getRole().trim().toUpperCase())
                .estatus(1)
                .creado(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Mexico_City"))))
                .build();
        RoleResponse roleResponse = new RoleResponse(repository.save(role));
        log.info("se regresa el role guardado");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.ROLE_SAVED, roleResponse);
    }
}
