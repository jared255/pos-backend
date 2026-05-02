package com.fastfoodpos.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceProvider {
    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        return getDataSource(false);
    }

    public static DataSource getDataSource(boolean useLocal) {
        if (dataSource == null) {
            try (InputStream in = DataSourceProvider.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {
                if (in == null) {
                    throw new IllegalStateException("No se encontró application.properties");
                }

                Properties props = new Properties();
                props.load(in);

                HikariConfig cfg = new HikariConfig();

                cfg.setDriverClassName("org.postgresql.Driver");
                cfg.setJdbcUrl(getConfig(props, "db.url", "DB_URL"));
                cfg.setUsername(getConfig(props, "db.user", "DB_USER"));
                cfg.setPassword(getConfig(props, "db.pass", "DB_PASS"));
                cfg.setMaximumPoolSize(Integer.parseInt(getConfig(props, "db.pool.max", "DB_POOL_MAX")));
                cfg.setAutoCommit(Boolean.parseBoolean(getConfig(props, "db.pool.auto-commit", "DB_POOL_AUTO_COMMIT")));
                cfg.setConnectionTimeout(Long.parseLong(getConfig(props, "db.pool.connection-timeout-ms", "DB_POOL_CONNECTION_TIMEOUT_MS")));
                dataSource = new HikariDataSource(cfg);
            } catch (Exception e) {
                throw new RuntimeException("Error configurando DataSource", e);
            }
        }
        return dataSource;
    }

    private static String getConfig(Properties props, String propertyKey, String envKey) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return props.getProperty(propertyKey);
    }
}
