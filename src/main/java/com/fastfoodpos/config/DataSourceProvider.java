package com.fastfoodpos.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class DataSourceProvider {
    private static HikariDataSource dataSource;

    @Bean
    public DataSource dataSource(Environment environment) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(readConfig(environment, "db.url", "DB_URL"));
        cfg.setUsername(readConfig(environment, "db.user", "DB_USER"));
        cfg.setPassword(readConfig(environment, "db.pass", "DB_PASS"));
        cfg.setMaximumPoolSize(readIntConfig(environment, "db.pool.max", 10));
        cfg.setConnectionTimeout(readLongConfig(environment, "db.pool.connection-timeout", 30000L));
        cfg.setInitializationFailTimeout(readLongConfig(environment, "db.pool.initialization-fail-timeout", -1L));
        cfg.setAutoCommit(true);
        return new HikariDataSource(cfg);
    }

    public static DataSource getDataSource(boolean useLocal) {

        if (dataSource == null) {
            try {
                Properties props = loadProperties(useLocal ? "application-local.properties" : "application-supabase.properties");

                HikariConfig cfg = new HikariConfig();
                if (useLocal) {
                    cfg.setJdbcUrl(readConfig(props, "db.local.url", "DB_LOCAL_URL"));
                    cfg.setUsername(readConfig(props, "db.local.user", "DB_LOCAL_USER"));
                    cfg.setPassword(readConfig(props, "db.local.pass", "DB_LOCAL_PASS"));
                } else {
                    cfg.setJdbcUrl(readConfig(props, "db.remote.url", "DB_REMOTE_URL"));
                    cfg.setUsername(readConfig(props, "db.remote.user", "DB_REMOTE_USER"));
                    cfg.setPassword(readConfig(props, "db.remote.pass", "DB_REMOTE_PASS"));
                }
                cfg.setMaximumPoolSize(
                        Integer.parseInt(props.getProperty("db.pool.max", "10"))
                );
                cfg.setAutoCommit(true);
                cfg.setInitializationFailTimeout(-1);
                dataSource = new HikariDataSource(cfg);
            } catch (Exception e) {
                throw new RuntimeException("Error configurando DataSource", e);
            }
        }
        return dataSource;
    }

    private static Properties loadProperties(String profileProperties) throws Exception {
        Properties props = new Properties();
        loadPropertiesFile(props, "application.properties");
        loadPropertiesFile(props, profileProperties);
        return props;
    }

    private static void loadPropertiesFile(Properties props, String fileName) throws Exception {
        try (InputStream in = DataSourceProvider.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in != null) {
                props.load(in);
            }
        }
    }

    private static String readConfig(Environment environment, String propertyName, String environmentName) {
        String value = System.getenv(environmentName);
        if (value == null || value.isBlank()) {
            value = environment.getProperty(propertyName);
        }
        return value;
    }

    private static int readIntConfig(Environment environment, String propertyName, int defaultValue) {
        return environment.getProperty(propertyName, Integer.class, defaultValue);
    }

    private static long readLongConfig(Environment environment, String propertyName, long defaultValue) {
        return environment.getProperty(propertyName, Long.class, defaultValue);
    }

    private static String readConfig(Properties props, String propertyName, String environmentName) {
        String value = System.getenv(environmentName);
        if (value == null || value.isBlank()) {
            value = props.getProperty(propertyName);
        }
        return value;
    }
}
