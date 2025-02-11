package com.nm.order.management.userservice.infra.config;

import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

import static com.nm.order.management.userservice.infra.config.UserServiceDatabaseConfig.USER_SERVICE_DATA_SOURCE_BEAN;

@RequiredArgsConstructor
@Configuration
public class UserLiquibaseConfig {

    private final Environment environment;

    @Bean
    public SpringLiquibase userServiceLiquibase(@Qualifier(USER_SERVICE_DATA_SOURCE_BEAN) DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:/database/user-service-master.xml");
        liquibase.setDataSource(dataSource);
        liquibase.setDropFirst(false);

        return liquibase;
    }
}
