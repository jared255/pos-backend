package com.fastfoodpos.infrastructure.config;

import com.fastfoodpos.application.service.ManageProductService;
import com.fastfoodpos.domain.port.in.ManageProductPort;
import com.fastfoodpos.domain.port.out.ProductRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductUseCaseConfig {
    @Bean
    public ManageProductPort manageProductPort(ProductRepositoryPort productRepositoryPort) {
        return new ManageProductService(productRepositoryPort);
    }
}
