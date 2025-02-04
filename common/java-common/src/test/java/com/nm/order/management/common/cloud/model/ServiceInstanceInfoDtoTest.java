package com.nm.order.management.common.cloud.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.client.ServiceInstance;

import java.util.function.Function;

import static org.mockito.Mockito.*;

class ServiceInstanceInfoDtoTest {


    @Mock
    private ServiceInstance mockServiceInstance;

    @Mock
    private CommonProtoChannel mockChannel;

    private Function<ServiceInstance, String> createGrpcServerAddressFunction;
    private Function<String, CommonProtoChannel> establishGrpcClientFunction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createGrpcServerAddressFunction = mock(Function.class);
        establishGrpcClientFunction = mock(Function.class);
    }

    @Test
    void shouldCreateServiceInfoDtoSuccessfully() {
        String grpcAddress = "grpc://localhost:50051";

        when(createGrpcServerAddressFunction.apply(mockServiceInstance)).thenReturn(grpcAddress);
        when(establishGrpcClientFunction.apply(grpcAddress)).thenReturn(mockChannel);

        ServiceInstanceInfoDto result = ServiceInstanceInfoDto.createServiceInfoDto(
                mockServiceInstance,
                createGrpcServerAddressFunction,
                establishGrpcClientFunction
        );

        assertNotNull(result);
        assertEquals(grpcAddress, result.getGrpcAddress());
        assertEquals(mockChannel, result.getChannel());

        verify(createGrpcServerAddressFunction).apply(mockServiceInstance);
        verify(establishGrpcClientFunction).apply(grpcAddress);
    }

    @Test
    void shouldHandleNullGrpcAddressGracefully() {
        when(createGrpcServerAddressFunction.apply(mockServiceInstance)).thenReturn(null);

        ServiceInstanceInfoDto result = ServiceInstanceInfoDto.createServiceInfoDto(
                mockServiceInstance,
                createGrpcServerAddressFunction,
                establishGrpcClientFunction
        );

        assertNotNull(result);
        assertNull(result.getGrpcAddress());
        assertNull(result.getChannel());

        verify(createGrpcServerAddressFunction).apply(mockServiceInstance);
        verify(establishGrpcClientFunction, never()).apply(anyString());
    }

    @Test
    void shouldHandleNullChannelGracefully() {
        String grpcAddress = "grpc://localhost:50051";
        when(createGrpcServerAddressFunction.apply(mockServiceInstance)).thenReturn(grpcAddress);
        when(establishGrpcClientFunction.apply(grpcAddress)).thenReturn(null);

        ServiceInstanceInfoDto result = ServiceInstanceInfoDto.createServiceInfoDto(
                mockServiceInstance,
                createGrpcServerAddressFunction,
                establishGrpcClientFunction
        );

        assertNotNull(result);
        assertEquals(grpcAddress, result.getGrpcAddress());
        assertNull(result.getChannel());

        verify(createGrpcServerAddressFunction).apply(mockServiceInstance);
        verify(establishGrpcClientFunction).apply(grpcAddress);
    }


}