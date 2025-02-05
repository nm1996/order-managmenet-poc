package com.nm.order.management.common.grpc.exception;

public class ServiceUnavailableException extends Exception {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
