package com.nm.order.management.userservice.infra.config;

import com.nm.order.management.common.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

import static com.nm.order.management.userservice.infra.config.UserServiceDatabaseConfig.USER_SERVICE_ENTITY_MANAGER_FACTORY;
import static com.nm.order.management.userservice.infra.config.UserServiceDatabaseConfig.USER_SERVICE_TRANSACTION_MANAGER;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.nm.order.management.userservice.repository",
        entityManagerFactoryRef = USER_SERVICE_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = USER_SERVICE_TRANSACTION_MANAGER
)
public class UserServiceDatabaseConfig extends DatabaseConfig {

    public static final String USER_SERVICE_DATA_SOURCE_BEAN = "userServiceDataSource";
    public static final String USER_SERVICE_ENTITY_MANAGER_FACTORY = "userServiceEntityManagerFactory";
    public static final String USER_SERVICE_TRANSACTION_MANAGER = "userServiceTransactionManager";

    public UserServiceDatabaseConfig(Environment environment) {
        super(environment);
    }

    @Bean(USER_SERVICE_DATA_SOURCE_BEAN)
    public DataSource userServiceDataSourceBean() throws SQLException {
        HikariDataSource dataSource = new HikariDataSource();

        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s",
                getHost(),
                getPort(),
                getDatabase()
        );

        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(getUsername());
        dataSource.setPassword(getPassword());
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setMaximumPoolSize(10);

        return dataSource;
    }

    @Bean(USER_SERVICE_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean userServiceEntityManagerFactory(@Qualifier(USER_SERVICE_DATA_SOURCE_BEAN) DataSource dataSource) {
        boolean showSql = getShowSql();

        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);

        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(showSql);
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");

        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform");
        jpaProperties.put("hibernate.transaction.coordinator_class", "org.hibernate.transaction.JDBCTransactionFactory");
        jpaProperties.put("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
        localContainerEntityManagerFactoryBean.setJpaProperties(jpaProperties);

        return localContainerEntityManagerFactoryBean;
    }


    @Bean(USER_SERVICE_TRANSACTION_MANAGER)
    public JpaTransactionManager userServiceTransactionManager(
            @Qualifier(USER_SERVICE_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
