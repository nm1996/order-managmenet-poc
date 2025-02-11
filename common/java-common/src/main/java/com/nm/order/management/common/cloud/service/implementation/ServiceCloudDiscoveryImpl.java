package com.nm.order.management.common.cloud.service.implementation;


import com.nm.order.management.common.cloud.service.ServiceCloudDiscovery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceCloudDiscoveryImpl implements ServiceCloudDiscovery {

    private final DiscoveryClient discoveryClient;

    @Override
    public List<ServiceInstance> getServiceInstanceList(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        log.info("Discovered {} instance [serviceName={}]", instances.size(), serviceName);
        return instances;
    }
}
