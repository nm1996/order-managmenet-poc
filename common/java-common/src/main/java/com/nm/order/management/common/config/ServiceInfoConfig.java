package com.nm.order.management.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Configuration
public class ServiceInfoConfig {

    @Value("${nix.services}")
    private List<String> services;


    public Set<String> getAllServiceNames() {
        return new HashSet<>(services);
    }
}
