package com.nm.order.management.gateway.infra.exception;

import lombok.Getter;

@Getter
public class TargetUnavailableException extends RuntimeException {

    private final String targetService;

    public TargetUnavailableException(String message, String targetService) {
        super(message);
        this.targetService = targetService;
    }
}
