package com.tickets.usuarios.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickets.usuarios.constants.Constants;
import com.tickets.usuarios.dto.UserLogin;
import com.tickets.usuarios.dto.UserRequest;
import com.tickets.usuarios.dto.UserResponse;
import com.tickets.usuarios.entities.Role;
import com.tickets.usuarios.entities.User;
import com.tickets.usuarios.models.ResponseService;
import com.tickets.usuarios.repositories.RoleRepository;
import com.tickets.usuarios.repositories.UserRepository;
import com.tickets.usuarios.services.UserService;
import com.tickets.usuarios.services.auth.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
@RefreshScope
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Value("${app.maintenance.createUsers}")
    private boolean createUsers;

    @Override
    @Transactional
    public ResponseService createUser(UserRequest request) {
        log.info("se revisa si la variable permite crear usuarios");
        if (!createUsers) {
            log.info("no se pueden crear usuarios por configuración");
            return new ResponseService(Constants.RESPONSE_TYPE_WARNING, Constants.USERS_DISABLED, null);
        }
        // se revisa si ya existe el username
        log.info("se revisa que no exista el username");
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername().trim());
        if (userOptional.isPresent()) {
            log.info("el username ya existe.");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USERNAME_EXISTS, null);
        }
        // se obtiene el rol user para guardarlo con el usuario
        log.info("se obtiene el rol user para guardarlo con el usuario");
        Optional<Role> roleUser = roleRepository.findByRoleAndEstatus("ROLE_USER", 1);
        List<Role> roles = new ArrayList<>();
        roleUser.ifPresent(roles::add);
        // se revisa si se guardará como admin
        if (request.isAdmin()) {
            log.info("se obtiene el rol admin para guardarlo con el usuario");
            Optional<Role> roleAdmin = roleRepository.findByRoleAndEstatus("ROLE_ADMIN", 1);
            if (roleAdmin.isEmpty()) {
                log.info("el rol admin no existe");
                return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.ROLE_ADMIN_UNKNOWN, null);
            }
            roleAdmin.ifPresent(roles::add);
        }
        // se guarda el usuario
        log.info("se guarda el usuario");
        User user = User.builder()
                .nombre(request.getNombre().trim())
                .apaterno(request.getApaterno() == null ? "" : request.getApaterno().trim())
                .amaterno(request.getAmaterno() == null ? "" : request.getAmaterno().trim())
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .estatus(1)
                .creado(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Mexico_City"))))
                .roles(roles)
                .build();
        UserResponse userResponse = new UserResponse(userRepository.save(user));
        log.info("se regresa el usuario guardado");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USERNAME_SAVED, userResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseService getUSer(int userId) {
        log.info("se revisa el userId");
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.info("el userId no existe");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USERID_UNKNOWN, null);
        }
        log.info("obteniendo el usuario");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USER_OBTAINED, new UserResponse(userOptional.get()));
    }

    @Override
    @Transactional
    public ResponseService deleteUSer(int userId) {
        log.info("se revisa el userId");
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.info("el userId no existe");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USERID_UNKNOWN, null);
        }
        User user = userOptional.get();
        log.info("se revisa que esté activo el usuario");
        if (user.getEstatus() == 0) {
            log.info("el usuario ya se encuentra inactivo");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USER_DISABLED, null);
        }
        log.info("inhabilitando al usuario");
        user.setEstatus(0);
        user.setActualizado(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Mexico_City"))));
        UserResponse userResponse = new UserResponse(userRepository.save(user));
        log.info("se regresa el usuario inhabilitado");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USER_MODIFIED, userResponse);
    }

    @Override
    @Transactional
    public ResponseService modifyUSer(int userId) {
        log.info("se revisa el userId");
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.info("el userId no existe");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USERID_UNKNOWN, null);
        }
        User user = userOptional.get();
        log.info("se revisa que el usuario si se encuentre inhabilitado");
        if (user.getEstatus() == 1) {
            log.info("el usuario está habilitado");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USER_ENABLED, null);
        }
        log.info("habilitando al usuario");
        user.setEstatus(1);
        user.setActualizado(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Mexico_City"))));
        UserResponse userResponse = new UserResponse(userRepository.save(user));
        log.info("se regresa el usuario habilitado");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USER_MODIFIED, userResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseService login(UserLogin userLogin) {
        log.info("se verifica que exista el usuario");
        Optional<User> userOptional = userRepository.findByUsername(userLogin.getUsername());
        if (userOptional.isEmpty()) {
            log.info("no existe el usuario");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.INCORRECT_LOGIN, null);
        }
        User user = userOptional.get();
        log.info("se verifica la contrasena");
        boolean esCorrecta = passwordEncoder.matches(userLogin.getPassword(), user.getPassword());
        if (!esCorrecta) {
            log.info("la contrasena no coincide");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.INCORRECT_LOGIN, null);
        }
        log.info("se verifica que el estatus del usuario se encuentre habilitado");
        if (user.getEstatus() != 1) {
            log.info("el usuario está dado de baja");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.INCORRECT_LOGIN, null);
        }
        log.info("se comienza a crear el JWT");
        String jwt = jwtService.generateToken(user, generateExtraClaims(user));
        log.info("se regresa el jwt");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.CORRECT_LOGIN, jwt);
    }

    private Map<String, Object> generateExtraClaims(User user) {
        List<String> roles = user.getRoles().stream().map(Role::getRole).toList(); //Se crea una lista de roles
        Map<String, Object> extraClaims = new HashMap<>(); //Se generan los claims en base a los roles
        try {
            extraClaims.put("authorities", new ObjectMapper().writeValueAsString(roles));
        } catch (JsonProcessingException e) {
            log.info("error al escribir los roles");
            throw new RuntimeException(e);
        }
        return extraClaims;
    }
}
