package com.tickets.usuarios.dto;

import com.tickets.usuarios.entities.User;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserResponse implements Serializable {
    private int id;
    private String nombre;
    private String apaterno;
    private String amaterno;
    private String username;
    private int estatus;
    private String creado;
    private String modificado;
    private List<RoleResponse> roles = new ArrayList<>();

    public UserResponse(User user) {
        this.id = user.getId();
        this.nombre = user.getNombre();
        this.apaterno = user.getApaterno();
        this.amaterno = user.getAmaterno();
        this.username = user.getUsername();
        this.estatus = user.getEstatus();
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getCreado().getTime()), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.creado = dateTime.format(formatter);
        if (user.getActualizado() != null) {
            LocalDateTime dateModif = LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getActualizado().getTime()), ZoneId.systemDefault());
            this.modificado = dateModif.format(formatter);
        } else this.modificado = "";
        user.getRoles().forEach(role -> this.roles.add(new RoleResponse(role)));
    }
}
