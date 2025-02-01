package com.nm.order.management.common.version;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("com.nm.order.management.serviceinfo")
@Getter
@Configuration
public class VersionConfig {

    @Value("${version}")
    private String version;
}
