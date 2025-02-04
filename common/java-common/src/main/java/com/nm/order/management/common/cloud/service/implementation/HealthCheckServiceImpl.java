package com.nm.order.management.common.cloud.service.implementation;

import com.nm.order.management.common.cloud.config.ServiceCloudConfig;
import com.nm.order.management.common.cloud.service.HealthCheckService;
import com.nm.order.management.common.cloud.service.util.OrchestratorFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    private final static String healthActuator = "/actuator/health";
    private final WebClient webClient;

    public HealthCheckServiceImpl(@Qualifier(ServiceCloudConfig.SERVICE_BALANCE_WEB_CLIENT) WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public boolean isServiceUp(String address) {
        return check(address);
    }

    @Override
    public boolean isServiceUp(ServiceInstance instance) {
        String baseUrl = OrchestratorFunction.createServiceAddress(instance);
        return check(baseUrl);
    }

    private boolean check(String baseUrl) {
        String healthUrl = baseUrl + healthActuator;
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
