package com.tickets.usuarios.repositories;

import com.tickets.usuarios.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRole(String role);
    Optional<Role> findByRoleAndEstatus(String role, int estatus);
}
