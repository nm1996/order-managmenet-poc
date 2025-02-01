package com.nm.order.management.common.cloud;

import com.nm.order.management.common.ServiceInfoUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ServiceCloudOrchestrator {

    private final ConcurrentHashMap<String, ManagedChannel> clientCache = new ConcurrentHashMap<>();


    private final WebClient webClient;
    private final DiscoveryClient discoveryClient;

    @Autowired
    public ServiceCloudOrchestrator(@Qualifier(ServiceCloudConfig.SERVICE_BALANCE_WEB_CLIENT) WebClient webClient, DiscoveryClient discoveryClient) {
        this.webClient = webClient;
        this.discoveryClient = discoveryClient;
    }

    public List<ManagedChannel> getOrCreateGrpcClient(String serviceName) {
        List<ManagedChannel> channels = new ArrayList<>();

        getHealthyServiceAddress(serviceName).forEach(
                address -> {
                    ManagedChannel channel = clientCache.computeIfAbsent(address, this::createGrpcClient);
                    channels.add(channel);
                }
        );

        return channels;
    }


    private List<String> getHealthyServiceAddress(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

        if (instances.isEmpty()) {
            log.warn("No instances found [service={}]", serviceName);
            return List.of();
        }

        return instances.stream()
                .filter(instance -> isServiceUp(ServiceInfoUtil.getServiceHostPort(instance)))
                .map(instance -> STR."\{ServiceInfoUtil.getGrpcHost(instance)}:\{ServiceInfoUtil.getGrpcPort(instance)}")
                .toList();
    }


    private ManagedChannel createGrpcClient(String address) {
        log.info("Creating gRPC Client for: {}", address);
        return ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();
    }


    private boolean isServiceUp(String baseUrl) {
        String healthPath = "/actuator/health";
        String healthUrl = baseUrl + healthPath;
        try {
            Mono<String> response = webClient.get()
                    .uri(healthUrl)
                    .retrieve()
                    .bodyToMono(String.class);

            String healthStatus = response.block();
            return healthStatus != null && healthStatus.contains("\"status\":\"UP\"");
        } catch (Exception e) {
            log.warn("Checking service up failed [msg={}]", e.getMessage());
            return false;
        }
    }

}
