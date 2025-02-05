package com.nm.order.management.gateway;

import com.nm.order.management.common.config.CloudConfig;
import com.nm.order.management.common.config.MapperConfig;
import com.nm.order.management.common.config.ServiceInfoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@Import({
        CloudConfig.class,
        ServiceInfoConfig.class,
        MapperConfig.class
})
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayService {
    public static void main(String[] args) {
        SpringApplication.run(GatewayService.class);
    }
}