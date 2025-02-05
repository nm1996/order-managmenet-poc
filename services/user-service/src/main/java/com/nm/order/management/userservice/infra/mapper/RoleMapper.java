package com.nm.order.management.userservice.infra.mapper;

import com.nm.order.management.userservice.domain.Role;
import com.nm.order.management.common.mapper.exception.MappingException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public String toString(Role role) {
        return role.getName();
    }

    public Role toEnum(@NotNull String role) throws MappingException {
        return switch (role.toLowerCase()) {
            case "admin" -> Role.ADMIN;
            case "user" -> Role.USER;
            default -> throw new MappingException("Unexpected value: " + role.toLowerCase());
        };
    }
}
