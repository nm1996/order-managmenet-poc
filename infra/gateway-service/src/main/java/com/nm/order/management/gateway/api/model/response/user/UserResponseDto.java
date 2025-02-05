package com.nm.order.management.gateway.api.model.response.user;

import java.util.Date;

public record UserResponseDto(
        String username,
        String email,
        String fullName,
        Date dateOfBirth,
        String role
) {
}
