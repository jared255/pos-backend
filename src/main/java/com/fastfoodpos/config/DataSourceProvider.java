package com.fastfoodpos.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class DataSourceProvider {
    private static HikariDataSource dataSource;

    @Bean
    public DataSource dataSource() {
        return getDataSource(false);
    }

    public static DataSource getDataSource(boolean useLocal) {

        if (dataSource == null) {
            try (InputStream in = DataSourceProvider.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {
                Properties props = new Properties();
                props.load(in);

                HikariConfig cfg = new HikariConfig();
                if (useLocal) {
                    cfg.setJdbcUrl(readConfig(props, "db.local.url", "DB_LOCAL_URL"));
                } else {
                    cfg.setJdbcUrl(readConfig(props, "db.remote.url", "DB_REMOTE_URL"));
                    cfg.setUsername(readConfig(props, "db.remote.user", "DB_REMOTE_USER"));
                    cfg.setPassword(readConfig(props, "db.remote.pass", "DB_REMOTE_PASS"));
                }
                cfg.setMaximumPoolSize(
                        Integer.parseInt(props.getProperty("db.pool.max", "10"))
                );
                cfg.setAutoCommit(true);
                dataSource = new HikariDataSource(cfg);
            } catch (Exception e) {
                throw new RuntimeException("Error configurando DataSource", e);
            }
        }
        return dataSource;
    }

    private static String readConfig(Properties props, String propertyName, String environmentName) {
        String value = System.getenv(environmentName);
        if (value == null || value.isBlank()) {
            value = props.getProperty(propertyName);
        }
        return value;
    }
}
