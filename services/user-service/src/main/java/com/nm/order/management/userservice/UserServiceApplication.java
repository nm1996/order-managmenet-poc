package com.nm.order.management.userservice;

import com.nm.order.management.common.config.MapperConfig;
import com.nm.order.management.common.version.VersionConfig;
import com.nm.order.management.userservice.infra.config.UserLiquibaseConfig;
import com.nm.order.management.userservice.infra.config.UserServiceDatabaseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@Import({
        VersionConfig.class,
        UserServiceDatabaseConfig.class,
        UserLiquibaseConfig.class,
        MapperConfig.class
})
@EnableDiscoveryClient
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class);
    }


}



