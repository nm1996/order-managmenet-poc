package com.nm.order.management.common.cloud.service.implementation;

import com.nm.order.management.common.ServiceInfoUtil;
import com.nm.order.management.common.cloud.model.CommonProtoChannel;
import com.nm.order.management.common.cloud.model.ServiceCloudInfoDto;
import com.nm.order.management.common.cloud.model.ServiceInstanceInfoDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.ServiceInstance;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ConnectionOrchestratorImplTest {

    MockedStatic<ServiceInfoUtil> serviceInfoUtilMockedStatic;
    MockedStatic<CommonProtoChannel> protoChannelMockedStatic;

    @InjectMocks
    private ConnectionOrchestratorImpl connectionOrchestrator;

    @Mock
    private Function<String, List<ServiceInstance>> getServiceInstanceFunction;

    @Mock
    private Predicate<ServiceInstance> filterServiceUp;

    @BeforeEach
    void setUp() {
        serviceInfoUtilMockedStatic = Mockito.mockStatic(ServiceInfoUtil.class);
        protoChannelMockedStatic = Mockito.mockStatic(CommonProtoChannel.class);
    }

    @AfterEach
    void tearDown() {
        serviceInfoUtilMockedStatic.close();
        protoChannelMockedStatic.close();
    }

    @Test
    public void testConnectAndCreateInfoDto_ServiceUp() {
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        CommonProtoChannel commonProtoChannel = mock(CommonProtoChannel.class);
        String serviceAddress = "localhost:8080";
        String grpcServerAddress = "grpc://localhost:9090";

        when(getServiceInstanceFunction.apply("service1")).thenReturn(List.of(serviceInstance));
        when(filterServiceUp.test(serviceInstance)).thenReturn(true);


        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getServiceHostPort(serviceInstance)).thenReturn("localhost:8080");
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getGrpcPort(serviceInstance)).thenReturn("9090");
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getGrpcHost(serviceInstance)).thenReturn("localhost");
        protoChannelMockedStatic.when(() -> CommonProtoChannel.createCommonProtoChannel(grpcServerAddress)).thenReturn(commonProtoChannel);

        ServiceCloudInfoDto result = connectionOrchestrator.connectAndCreateInfoDto("service1", getServiceInstanceFunction, filterServiceUp);

        assertNotNull(result);
        assertTrue(result.getInfoDtoMap().containsKey(serviceAddress));
    }

    @Test
    public void testConnectAndCreateInfoDto_ServiceDown() {
        when(getServiceInstanceFunction.apply("service1")).thenReturn(List.of());

        ServiceCloudInfoDto result = connectionOrchestrator.connectAndCreateInfoDto("service1", getServiceInstanceFunction, filterServiceUp);

        assertNotNull(result);
        assertTrue(result.getInfoDtoMap().isEmpty());
    }


    @Test
    public void testMaintenanceConnection_Reconnected() {
        CommonProtoChannel commonProtoChannel = mock(CommonProtoChannel.class);
        Map<String, ServiceInstanceInfoDto> infoDtoMap = new HashMap<>();
        infoDtoMap.put("localhost:8080", ServiceInstanceInfoDto.builder()
                .grpcAddress("localhost:9090")
                .channel(commonProtoChannel)
                .build());

        ServiceCloudInfoDto existingDto = ServiceCloudInfoDto.createUpServiceInfo("test-service", infoDtoMap);

        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        when(getServiceInstanceFunction.apply("test-service")).thenReturn(List.of(serviceInstance));
        when(filterServiceUp.test(serviceInstance)).thenReturn(true);

        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getServiceHostPort(serviceInstance)).thenReturn("localhost:8080");
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getGrpcPort(serviceInstance)).thenReturn("9090");
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getGrpcHost(serviceInstance)).thenReturn("localhost");

        ServiceCloudInfoDto updatedDto = connectionOrchestrator.maintenanceConnection(existingDto, getServiceInstanceFunction, filterServiceUp);

        assertEquals(updatedDto, existingDto);
    }

    @Test
    public void testMaintenanceConnection_ReconnectAndDrop() {
        CommonProtoChannel commonProtoChannel = mock(CommonProtoChannel.class);
        Map<String, ServiceInstanceInfoDto> infoDtoMap = new HashMap<>();
        infoDtoMap.put("localhost:8081", ServiceInstanceInfoDto.builder()
                .grpcAddress("localhost:9091")
                .channel(commonProtoChannel)
                .build());
        infoDtoMap.put("localhost:8080", ServiceInstanceInfoDto.builder()
                .grpcAddress("localhost:9090")
                .channel(commonProtoChannel)
                .build());

        ServiceCloudInfoDto existingDto = ServiceCloudInfoDto.createUpServiceInfo("test-service", infoDtoMap);

        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        when(getServiceInstanceFunction.apply("test-service")).thenReturn(List.of(serviceInstance));
        when(filterServiceUp.test(serviceInstance)).thenReturn(true);

        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getServiceHostPort(serviceInstance)).thenReturn("localhost:8080");
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getGrpcPort(serviceInstance)).thenReturn("9090");
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getGrpcHost(serviceInstance)).thenReturn("localhost");

        ServiceCloudInfoDto updatedDto = connectionOrchestrator.maintenanceConnection(existingDto, getServiceInstanceFunction, filterServiceUp);

        assertEquals(updatedDto, existingDto);
        assertEquals(1, updatedDto.getInfoDtoMap().size());
    }

    @Test
    public void testMapDiscoveredServices() {
        ServiceInstance serviceInstance1 = mock(ServiceInstance.class);
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getServiceHostPort(serviceInstance1)).thenReturn("localhost:8080");
        ServiceInstance serviceInstance2 = mock(ServiceInstance.class);
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getServiceHostPort(serviceInstance2)).thenReturn("localhost:8081");

        List<ServiceInstance> instances = List.of(serviceInstance1, serviceInstance2);

        Map<String, ServiceInstance> result = connectionOrchestrator.mapDiscoveredServices(instances);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("localhost:8080"));
        assertTrue(result.containsKey("localhost:8081"));
    }

    @Test
    public void testRemoveUnavailableServices() {
        ServiceCloudInfoDto existingCloudInfoDto = mock(ServiceCloudInfoDto.class);
        Set<String> knownAddresses = new HashSet<>();
        knownAddresses.add("localhost:8080");

        Map<String, ServiceInstance> discoveredServices = Map.of();

        connectionOrchestrator.removeUnavailableServices(existingCloudInfoDto, knownAddresses, discoveredServices);
    }


    @Test
    public void testUpdateServiceCloudInfo() {
        ServiceCloudInfoDto existingDto = mock(ServiceCloudInfoDto.class);
        Map<String, ServiceInstanceInfoDto> infoDtoMap = new HashMap<>();
        when(existingDto.getInfoDtoMap()).thenReturn(infoDtoMap);

        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getServiceHostPort(serviceInstance)).thenReturn("localhost:8080");
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getGrpcPort(serviceInstance)).thenReturn("9090");
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getGrpcHost(serviceInstance)).thenReturn("localhost");
        Map<String, ServiceInstance> discoveredServices = Map.of("localhost:8080", serviceInstance);

        connectionOrchestrator.updateServiceCloudInfo(existingDto, discoveredServices);

        verify(existingDto, times(1)).getInfoDtoMap();
    }


}