package com.nm.order.management.common.cloud.model;

import static org.junit.jupiter.api.Assertions.*;


import io.grpc.ManagedChannel;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

class ServiceCloudInfoDtoTest {

    @Mock
    private CommonProtoChannel mockChannel;

    @Mock
    private ServiceInstanceInfoDto mockInstanceInfoDto;

    public ServiceCloudInfoDtoTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateUpServiceInfoWithProvidedInfo() {
        String serviceName = "test-service";
        Map<String, ServiceInstanceInfoDto> infoMap = new HashMap<>();
        infoMap.put("service1", mockInstanceInfoDto);

        ServiceCloudInfoDto dto = ServiceCloudInfoDto.createUpServiceInfo(serviceName, infoMap);

        assertNotNull(dto);
        assertEquals(serviceName, dto.getServiceName());
        assertEquals(infoMap, dto.getInfoDtoMap());
    }

    @Test
    void shouldCreateDownServiceInfoWithEmptyInfoMap() {
        String serviceName = "down-service";

        ServiceCloudInfoDto dto = ServiceCloudInfoDto.createDownServiceInfo(serviceName);

        assertNotNull(dto);
        assertEquals(serviceName, dto.getServiceName());
        assertTrue(dto.getInfoDtoMap().isEmpty());
    }

    @Test
    void shouldReturnEstablishedConnections() {
        ManagedChannel managedChannel = mock(ManagedChannel.class);
        String serviceName = "test-service";
        Map<String, ServiceInstanceInfoDto> infoMap = new HashMap<>();
        when(mockInstanceInfoDto.getChannel()).thenReturn(mockChannel);
        when(mockChannel.getChannel()).thenReturn(managedChannel);
        infoMap.put("service1", mockInstanceInfoDto);

        ServiceCloudInfoDto dto = ServiceCloudInfoDto.createUpServiceInfo(serviceName, infoMap);

        Set<ManagedChannel> channels = dto.getEstablishedConnections();

        assertNotNull(channels);
        assertEquals(1, channels.size());
        assertTrue(channels.contains(managedChannel));
    }

    @Test
    void shouldReturnServiceAddresses() {
        String serviceName = "test-service";
        Map<String, ServiceInstanceInfoDto> infoMap = new HashMap<>();
        infoMap.put("address1", mockInstanceInfoDto);
        infoMap.put("address2", mockInstanceInfoDto);

        ServiceCloudInfoDto dto = ServiceCloudInfoDto.createUpServiceInfo(serviceName, infoMap);

        Set<String> addresses = dto.getServiceAddresses();

        assertNotNull(addresses);
        assertEquals(2, addresses.size());
        assertTrue(addresses.contains("address1"));
        assertTrue(addresses.contains("address2"));
    }

}