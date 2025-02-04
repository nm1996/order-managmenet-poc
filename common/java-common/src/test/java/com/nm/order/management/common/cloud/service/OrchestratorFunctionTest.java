package com.nm.order.management.common.cloud.service;

import com.nm.order.management.common.cloud.model.CommonProtoChannel;
import com.nm.order.management.common.cloud.model.ServiceCloudInfoDto;
import com.nm.order.management.common.cloud.model.ServiceInstanceInfoDto;
import com.nm.order.management.common.cloud.service.util.OrchestratorFunction;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.client.ServiceInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrchestratorFunctionTest {
    @Mock
    private ServiceInstance mockServiceInstance;

    @Mock
    private CommonProtoChannel mockChannel;

    @Mock
    private ManagedChannel mockManagedChannel;

    @Mock
    private ServiceCloudInfoDto mockCloudInfoDto;

    private Map<String, ServiceInstanceInfoDto> infoDtoMap;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        infoDtoMap = new HashMap<>();
        when(mockCloudInfoDto.getInfoDtoMap()).thenReturn(infoDtoMap);
    }

    @Test
    void shouldCreateGrpcClientChannel() {
        String address = "localhost:50051";
        CommonProtoChannel channel = OrchestratorFunction.createGrpcClientChannel(address);
        assertNotNull(channel);
        channel.getChannel().shutdown();
    }

    @Test
    void shouldCreateServiceAddress() {
        when(mockServiceInstance.getMetadata()).thenReturn(Map.of("service-host-port", "127.0.0.1:8080"));
        String address = OrchestratorFunction.createServiceAddress(mockServiceInstance);
        assertEquals("127.0.0.1:8080", address);
    }

    @Test
    void shouldCreateGrpcServerAddress() {
        when(mockServiceInstance.getMetadata()).thenReturn(Map.of(
                "grpc-host", "127.0.0.1",
                "grpc-port", "9090"));
        String grpcAddress = OrchestratorFunction.createGrpcServerAddress(mockServiceInstance);
        assertEquals("127.0.0.1:9090", grpcAddress);
    }

    @Test
    void shouldShutdownCloudInfoDto() throws InterruptedException {
        ServiceInstanceInfoDto mockInstanceInfo = mock(ServiceInstanceInfoDto.class);
        when(mockInstanceInfo.getChannel()).thenReturn(mockChannel);
        infoDtoMap.put("service1", mockInstanceInfo);
        when(mockChannel.getChannel()).thenReturn(mockManagedChannel);
        when(mockManagedChannel.shutdown()).thenReturn(mockManagedChannel);
        when(mockManagedChannel.awaitTermination(3, TimeUnit.SECONDS)).thenReturn(true);

        OrchestratorFunction.shutdown(mockCloudInfoDto);

        assertTrue(infoDtoMap.isEmpty());
    }

    @Test
    void shouldRemoveUnavailableServices() throws InterruptedException {
        ServiceInstanceInfoDto mockInstanceInfo = mock(ServiceInstanceInfoDto.class);
        when(mockInstanceInfo.getChannel()).thenReturn(mockChannel);
        infoDtoMap.put("service1", mockInstanceInfo);
        when(mockChannel.getChannel()).thenReturn(mockManagedChannel);
        when(mockManagedChannel.shutdown()).thenReturn(mockManagedChannel);
        when(mockManagedChannel.awaitTermination(3, TimeUnit.SECONDS)).thenReturn(true);

        OrchestratorFunction.removeUnavailableServices(mockCloudInfoDto, Set.of("service1"));

        assertTrue(infoDtoMap.isEmpty());
    }
}