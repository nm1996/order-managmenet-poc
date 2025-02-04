package com.nm.order.management.common.cloud.service;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

public interface ServiceCloudDiscovery {

    List<ServiceInstance> getServiceInstanceList(String serviceName);
}
