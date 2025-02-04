package com.nm.order.management.common.cloud.service.implementation;


import com.nm.order.management.common.cloud.service.ServiceCloudDiscovery;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ServiceCloudDiscoveryImpl implements ServiceCloudDiscovery {

    private final DiscoveryClient discoveryClient;

    @Override
    public List<ServiceInstance> getServiceInstanceList(String serviceName) {
        return discoveryClient.getInstances(serviceName);
    }
}
