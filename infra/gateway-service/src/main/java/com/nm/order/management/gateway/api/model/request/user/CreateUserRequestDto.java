package com.nm.order.management.gateway.api.model.request.user;


public record CreateUserRequestDto(
        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        Long dateOfBirthTimestamp,
        String role
) {
}
