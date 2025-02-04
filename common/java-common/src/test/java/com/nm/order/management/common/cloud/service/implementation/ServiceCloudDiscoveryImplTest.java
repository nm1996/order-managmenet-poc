package com.nm.order.management.common.cloud.service.implementation;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceCloudDiscoveryImplTest {

    @InjectMocks
    ServiceCloudDiscoveryImpl serviceCloudDiscovery;

    @Mock
    DiscoveryClient discoveryClient;

    @Test
    public void testEmptyResponse() {
        List<ServiceInstance> result = List.of();
        String serviceName = "test-service";
        when(discoveryClient.getInstances(serviceName)).thenReturn(result);

        List<ServiceInstance> realResult = serviceCloudDiscovery.getServiceInstanceList(serviceName);
        assertEquals(0, realResult.size());
    }

    @Test
    public void testResponse() {
        List<ServiceInstance> result = List.of(mock(ServiceInstance.class));
        String serviceName = "test-service";
        when(discoveryClient.getInstances(serviceName)).thenReturn(result);

        List<ServiceInstance> realResult = serviceCloudDiscovery.getServiceInstanceList(serviceName);
        assertEquals(1, realResult.size());
    }
}