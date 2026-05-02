package com.fastfoodpos.infrastructure.config;

import com.fastfoodpos.application.service.ManageProductService;
import com.fastfoodpos.config.DataSourceProvider;
import com.fastfoodpos.domain.port.in.ManageProductPort;
import com.fastfoodpos.domain.port.out.ProductRepositoryPort;
import com.fastfoodpos.infrastructure.persistence.jdbc.JdbcProductRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DevRunner {

    @Bean
    public DataSource dataSource() {
        return DataSourceProvider.getDataSource();
    }

    @Bean
    public ProductRepositoryPort productRepositoryPort(DataSource dataSource) {
        return new JdbcProductRepositoryAdapter(dataSource);
    }

    @Bean
    public ManageProductPort manageProductPort(ProductRepositoryPort productRepositoryPort) {
        return new ManageProductService(productRepositoryPort);
    }
}
