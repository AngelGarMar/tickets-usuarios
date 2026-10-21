package com.tickets.usuarios.services;

import com.tickets.usuarios.dto.UserLogin;
import com.tickets.usuarios.dto.UserRequest;
import com.tickets.usuarios.models.ResponseService;

public interface UserService {
    ResponseService createUser(UserRequest request);
    ResponseService getUSer(int userId);
    ResponseService deleteUSer(int userId);
    ResponseService modifyUSer(int userId);
    ResponseService login(UserLogin uerLogin);
}
