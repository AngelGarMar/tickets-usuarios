package com.tickets.usuarios.controllers;

import com.tickets.usuarios.constants.Constants;
import com.tickets.usuarios.dto.RoleRequest;
import com.tickets.usuarios.models.ResponseService;
import com.tickets.usuarios.services.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets/roles")
public class RoleController {
    @Autowired
    private RoleService service;

    @PostMapping
    public ResponseEntity<ResponseService> createRole(@RequestBody @Valid RoleRequest request, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        ResponseService response = service.createRole(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<ResponseService> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });
        return new ResponseEntity<>(new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.MISSED_INFORMATION, errors), HttpStatus.OK);
    }
}
