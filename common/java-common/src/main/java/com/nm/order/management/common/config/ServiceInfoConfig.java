package com.nm.order.management.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;



@Getter
@Configuration
public class ServiceInfoConfig {

    @Value("${service.info.user}")
    private String userServiceName;

    @Value("${service.info.product}")
    private String productServiceName;

    @Value("${service.info.order}")
    private String orderServiceName;

    @Value("${service.info.analytic}")
    private String analyticServiceName;

    @Value("${service.info.notification}")
    private String notificationServiceName;
}
