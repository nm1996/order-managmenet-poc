package com.nm.order.management.common.cloud.service.implementation;

import com.nm.order.management.common.cloud.model.ServiceCloudInfoDto;
import com.nm.order.management.common.cloud.service.HealthCheckService;
import com.nm.order.management.common.cloud.service.OrchestratorConnection;
import com.nm.order.management.common.cloud.service.ServiceCloudDiscovery;
import com.nm.order.management.common.cloud.service.util.OrchestratorFunction;
import com.nm.order.management.common.cloud.service.ServiceCloudOrchestrator;
import com.nm.order.management.common.config.ServiceInfoConfig;
import io.grpc.ManagedChannel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ServiceCloudOrchestratorImpl implements ServiceCloudOrchestrator {

    final Map<String, ServiceCloudInfoDto> clientCache;

    private final HealthCheckService healthCheckService;
    private final ServiceCloudDiscovery serviceCloudDiscovery;
    private final OrchestratorConnection connectionOrchestrator;
    private final ServiceInfoConfig serviceInfoConfig;

    @Autowired
    public ServiceCloudOrchestratorImpl(HealthCheckService healthCheckService,
                                        ServiceCloudDiscovery serviceCloudDiscovery,
                                        OrchestratorConnection connectionOrchestrator,
                                        ServiceInfoConfig serviceInfoConfig) {
        this.healthCheckService = healthCheckService;
        this.serviceCloudDiscovery = serviceCloudDiscovery;
        this.connectionOrchestrator = connectionOrchestrator;
        this.serviceInfoConfig = serviceInfoConfig;
        clientCache = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void onInitPopulateMap() {
        log.info("Initial try to establish connections...");
        clientCache.putAll(createInitialClientCache(getServiceNames()));
    }

    @Scheduled(fixedRate = 30_000)
    public void refreshGrpcClients() {
        log.info("Running scheduled establish connection check...");
        updateClientCache(getServiceNames());
    }

    public void shutdownConnections(String serviceName) {
        ServiceCloudInfoDto serviceCloudInfoDto = clientCache.get(serviceName);
        OrchestratorFunction.shutdown(serviceCloudInfoDto);
    }

    public Set<ManagedChannel> getChannelsByServiceName(String serviceName) {
        ServiceCloudInfoDto cloudInfoDto = clientCache.get(serviceName);
        if (cloudInfoDto == null) {
            return Set.of();
        }
        return cloudInfoDto.getEstablishedConnections();
    }


    private Map<String, ServiceCloudInfoDto> createInitialClientCache(Set<String> knownServices) {
        return knownServices.stream()
                .collect(Collectors.toMap(
                        serviceName -> serviceName,
                        serviceName -> connectionOrchestrator.connectAndCreateInfoDto(
                                serviceName,
                                serviceCloudDiscovery::getServiceInstanceList,
                                healthCheckService::isServiceUp
                        )
                ));
    }

    private void updateClientCache(Set<String> knownServices) {
        knownServices.forEach(
                serviceName -> {
                    ServiceCloudInfoDto updatedDto = connectionOrchestrator.maintenanceConnection(
                            clientCache.get(serviceName),
                            serviceCloudDiscovery::getServiceInstanceList,
                            healthCheckService::isServiceUp
                    );
                    clientCache.put(serviceName, updatedDto);
                }
        );
    }

    private Set<String> getServiceNames() {
        return new HashSet<>(serviceInfoConfig.getAllServiceNames());
    }


}