package com.nm.order.management.userservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    USER("USER"),
    ADMIN("ADMIN");

    private final String name;

}
