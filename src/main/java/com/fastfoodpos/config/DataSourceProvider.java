package com.fastfoodpos.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceProvider {
    private static HikariDataSource dataSource;

    public static DataSource getDataSource(boolean useLocal) {

        if (dataSource == null) {
            try (InputStream in = DataSourceProvider.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {
                Properties props = new Properties();
                props.load(in);

                HikariConfig cfg = new HikariConfig();
                if (useLocal) {
                    cfg.setJdbcUrl(props.getProperty("db.local.url"));
                } else {
                    cfg.setJdbcUrl(props.getProperty("db.remote.url"));
                    cfg.setUsername(props.getProperty("db.remote.user"));
                    cfg.setPassword(props.getProperty("db.remote.pass"));
                }
                cfg.setMaximumPoolSize(
                        Integer.parseInt(props.getProperty("db.pool.max", "10"))
                );
                cfg.setAutoCommit(false);
                dataSource = new HikariDataSource(cfg);
            } catch (Exception e) {
                throw new RuntimeException("Error configurando DataSource", e);
            }
        }
        return dataSource;
    }
}
