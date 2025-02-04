package com.nm.order.management.common.cloud.service.implementation;

import com.nm.order.management.common.ServiceInfoUtil;
import com.nm.order.management.common.cloud.model.CommonProtoChannel;
import com.nm.order.management.common.cloud.model.ServiceCloudInfoDto;
import com.nm.order.management.common.cloud.model.ServiceInstanceInfoDto;
import com.nm.order.management.common.cloud.service.HealthCheckService;
import com.nm.order.management.common.cloud.service.OrchestratorConnection;
import com.nm.order.management.common.cloud.service.ServiceCloudDiscovery;
import com.nm.order.management.common.config.ServiceInfoConfig;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.ServiceInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCloudOrchestratorImplTest {

    ServiceCloudOrchestratorImpl serviceCloudOrchestrator;

    @Mock
    HealthCheckService healthCheckService;
    @Mock
    ServiceCloudDiscovery serviceCloudDiscovery;
    @Mock
    OrchestratorConnection connectionOrchestrator;
    @Mock
    ServiceInfoConfig serviceInfoConfig;

    MockedStatic<ServiceInfoUtil> utilMockedStatic;


    @BeforeEach
    void setUp() {
        utilMockedStatic = mockStatic((ServiceInfoUtil.class));
        serviceCloudOrchestrator = new ServiceCloudOrchestratorImpl(
                healthCheckService,
                serviceCloudDiscovery,
                connectionOrchestrator,
                serviceInfoConfig
        );
    }

    @AfterEach
    void tearDown() {
        utilMockedStatic.close();
    }

    private ServiceCloudInfoDto prepareTestWithDiscoveredData(String serviceName,
                                                              ServiceInstance instance,
                                                              ServiceInstance instance2,
                                                              CommonProtoChannel channel,
                                                              CommonProtoChannel channel2) {
        Set<String> knownServices = Set.of(serviceName);

        createReturnOfServicesHosts(instance, instance2);
        ServiceCloudInfoDto serviceCloudInfoDto = ServiceCloudInfoDto.createUpServiceInfo(serviceName,
                createServiceInstanceInfoMap(channel, channel2));

        when(serviceInfoConfig.getAllServiceNames()).thenReturn(knownServices);

        createReturnOfServicesHosts(instance, instance2);

        when(connectionOrchestrator.connectAndCreateInfoDto(any(), any(), any())).thenReturn(serviceCloudInfoDto);

        return serviceCloudInfoDto;
    }

    @Test
    public void testInitialWithDiscoveredServices() {
        String serviceName = "test-service";

        ServiceInstance instance = mock(ServiceInstance.class);
        ServiceInstance instance2 = mock(ServiceInstance.class);

        CommonProtoChannel channel = mock(CommonProtoChannel.class);
        CommonProtoChannel channel2 = mock(CommonProtoChannel.class);

        prepareTestWithDiscoveredData(serviceName, instance, instance2, channel, channel2);

        serviceCloudOrchestrator.onInitPopulateMap();

        assertTrue(serviceCloudOrchestrator.clientCache.containsKey(serviceName));
        assertEquals(2, serviceCloudOrchestrator.clientCache.get(serviceName).getInfoDtoMap().size());
    }

    @Test
    public void testShutdownServices() throws InterruptedException {
        String serviceName = "test-service";

        ServiceInstance instance = mock(ServiceInstance.class);
        ServiceInstance instance2 = mock(ServiceInstance.class);

        CommonProtoChannel channel = mock(CommonProtoChannel.class);
        CommonProtoChannel channel2 = mock(CommonProtoChannel.class);

        prepareTestWithDiscoveredData(serviceName, instance, instance2, channel, channel2);

        shutdownChannel(channel);
        shutdownChannel(channel2);

        serviceCloudOrchestrator.onInitPopulateMap();
        serviceCloudOrchestrator.shutdownConnections(serviceName);
        assertTrue(serviceCloudOrchestrator.clientCache.containsKey(serviceName));
        assertEquals(0, serviceCloudOrchestrator.clientCache.get(serviceName).getInfoDtoMap().size());
    }

    @Test
    public void testUpdateClientCache_EverythingStaysSame() {
        String serviceName = "test-service";

        ServiceInstance instance = mock(ServiceInstance.class);
        ServiceInstance instance2 = mock(ServiceInstance.class);

        CommonProtoChannel channel = mock(CommonProtoChannel.class);
        CommonProtoChannel channel2 = mock(CommonProtoChannel.class);

        ServiceCloudInfoDto serviceCloudInfoDto = prepareTestWithDiscoveredData(serviceName, instance, instance2, channel, channel2);

        when(connectionOrchestrator.maintenanceConnection(any(), any(), any())).thenReturn(serviceCloudInfoDto);

        serviceCloudOrchestrator.onInitPopulateMap();
        serviceCloudOrchestrator.refreshGrpcClients();

        assertTrue(serviceCloudOrchestrator.clientCache.containsKey(serviceName));
        assertEquals(2, serviceCloudOrchestrator.clientCache.get(serviceName).getInfoDtoMap().size());
    }

    @Test
    public void testUpdateClientCache_OneDrop() {
        String serviceName = "test-service";

        ServiceInstance instance = mock(ServiceInstance.class);
        ServiceInstance instance2 = mock(ServiceInstance.class);

        CommonProtoChannel channel = mock(CommonProtoChannel.class);
        CommonProtoChannel channel2 = mock(CommonProtoChannel.class);

        ServiceCloudInfoDto serviceCloudInfoDto = prepareTestWithDiscoveredData(serviceName, instance, instance2, channel, channel2);
        serviceCloudInfoDto.getInfoDtoMap().remove("localhost:8001");
        when(connectionOrchestrator.maintenanceConnection(any(), any(), any())).thenReturn(serviceCloudInfoDto);


        serviceCloudOrchestrator.onInitPopulateMap();
        serviceCloudOrchestrator.refreshGrpcClients();

        assertTrue(serviceCloudOrchestrator.clientCache.containsKey(serviceName));
        assertEquals(1, serviceCloudOrchestrator.clientCache.get(serviceName).getInfoDtoMap().size());
    }

    @Test
    public void testGetChannelsByName_WithData() {
        String serviceName = "test-service";

        ServiceInstance instance = mock(ServiceInstance.class);
        ServiceInstance instance2 = mock(ServiceInstance.class);

        CommonProtoChannel channel = mock(CommonProtoChannel.class);
        CommonProtoChannel channel2 = mock(CommonProtoChannel.class);

        prepareTestWithDiscoveredData(serviceName, instance, instance2, channel, channel2);

        getChannels(channel);
        getChannels(channel2);

        serviceCloudOrchestrator.onInitPopulateMap();
        Set<ManagedChannel> channels = serviceCloudOrchestrator.getChannelsByServiceName(serviceName);

        assertEquals(2, channels.size());
    }

    @Test
    public void testGetChannelsByName_WithoutData() {
        String serviceName = "test-service";
        Set<ManagedChannel> channels = serviceCloudOrchestrator.getChannelsByServiceName(serviceName);

        assertEquals(0, channels.size());
    }

    private ManagedChannel getChannels(CommonProtoChannel channel) {
        ManagedChannel managedChannel = mock(ManagedChannel.class);
        when(channel.getChannel()).thenReturn(managedChannel);
        return managedChannel;
    }

    private void shutdownChannel(CommonProtoChannel channel) throws InterruptedException {
        ManagedChannel managedChannel = getChannels(channel);
        when((managedChannel.shutdown())).thenReturn(managedChannel);
        when(managedChannel.awaitTermination(3, TimeUnit.SECONDS)).thenReturn(true);
    }

    private Map<String, ServiceInstanceInfoDto> createServiceInstanceInfoMap(CommonProtoChannel channel,
                                                                             CommonProtoChannel channel2) {
        Map<String, ServiceInstanceInfoDto> result = new HashMap<>();
        result.put("localhost:8000", new ServiceInstanceInfoDto(channel, "localhost:9000"));
        result.put("localhost:8001", new ServiceInstanceInfoDto(channel2, "localhost:9001"));
        return result;
    }


    private void createReturnOfServicesHosts(ServiceInstance instance, ServiceInstance instance2) {
        utilMockedStatic.when(() -> ServiceInfoUtil.getServiceHostPort(instance)).thenReturn("localhost:8000");
        utilMockedStatic.when(() -> ServiceInfoUtil.getServiceHostPort(instance2)).thenReturn("localhost:8001");

    }
}
