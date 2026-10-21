package com.tickets.usuarios.controllers;

import com.tickets.usuarios.constants.Constants;
import com.tickets.usuarios.dto.UserLogin;
import com.tickets.usuarios.dto.UserRequest;
import com.tickets.usuarios.models.ResponseService;
import com.tickets.usuarios.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RefreshScope // para el actuator del pom
@Slf4j
public class UserController {
    @Autowired
    private UserService service;

    @Value("${app.maintenance.message: Sistema Operativo}")
    private String maintenanceMessage;

    @PostMapping("/users")
    public ResponseEntity<ResponseService> createUser(@RequestBody @Valid UserRequest request, BindingResult result, HttpServletResponse httpServletResponse) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        ResponseService response = service.createUser(request);
        httpServletResponse.addHeader("X-Maintenance-Message", maintenanceMessage);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseService> getUser(@PathVariable int userId, @RequestHeader("X-User-Id") String user, @RequestHeader("X-User-Roles") String roles) {
        log.info("usuario autenticado haciendo la petición: {}", user);
        log.info("roles del usuario: {}", roles);
        ResponseService response = service.getUSer(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ResponseService> deleteUser(@PathVariable int userId) {
        ResponseService response = service.deleteUSer(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<ResponseService> modifyUser(@PathVariable int userId) {
        ResponseService response = service.modifyUSer(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseService> login(@RequestBody @Valid UserLogin login, BindingResult result) {
        if (result.hasFieldErrors()) return validation(result);
        ResponseService response = service.login(login);
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
