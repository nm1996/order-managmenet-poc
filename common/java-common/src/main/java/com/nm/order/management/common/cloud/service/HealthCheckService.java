package com.nm.order.management.common.cloud.service;

import org.springframework.cloud.client.ServiceInstance;

public interface HealthCheckService {

    boolean isServiceUp(String address);
    boolean isServiceUp(ServiceInstance instance);
}
