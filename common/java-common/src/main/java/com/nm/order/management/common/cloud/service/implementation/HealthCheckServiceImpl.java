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
        boolean isServiceUp;
        String healthUrl = baseUrl + healthActuator;
        try {
            String healthStatus = webClient.get()
                    .uri(healthUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            isServiceUp = healthStatus != null && healthStatus.contains("\"status\":\"UP\"");
            log.info("Checking service up success [service={}]", baseUrl);
        } catch (Exception e) {
            log.warn("Checking service up failed [msg={}]", e.getMessage());
            isServiceUp = false;
        }

        return isServiceUp;
    }
}
