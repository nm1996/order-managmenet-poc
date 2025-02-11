package com.nm.order.management.common.database;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
public class DatabaseConfig {

    private final String username = "db.config.username";
    private final String password = "db.config.password";
    private final String database = "db.config.database";
    private final String host = "db.config.host";
    private final String port = "db.config.port";
    private final String showSql = "db.config.showSql";

    @Getter
    private final Environment environment;

    public String getUsername() {
        return environment.getProperty(username, String.class, "");
    }

    public String getDatabase() {
        return environment.getProperty(database, String.class, "");
    }

    public String getPassword() {
        return environment.getProperty(password, String.class, "");
    }

    public String getHost() {
        return environment.getProperty(host, String.class, "localhost");
    }

    public String  getPort() {
        return environment.getProperty(port, String.class, "3306");
    }


    public boolean getShowSql() {
        return environment.getProperty(showSql, Boolean.class, false);
    }
}
