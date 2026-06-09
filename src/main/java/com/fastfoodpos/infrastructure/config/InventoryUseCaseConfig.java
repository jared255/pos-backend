package com.fastfoodpos.infrastructure.config;

import com.fastfoodpos.inventory.application.service.ProcessOrderCreatedService;
import com.fastfoodpos.inventory.domain.port.in.ProcessOrderCreatedPort;
import com.fastfoodpos.inventory.domain.port.out.InventoryStockRepositoryPort;
import com.fastfoodpos.inventory.domain.port.out.OrderAuditRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryUseCaseConfig {
    @Bean
    public ProcessOrderCreatedPort processOrderCreatedPort(InventoryStockRepositoryPort inventoryStockRepositoryPort,
                                                           OrderAuditRepositoryPort orderAuditRepositoryPort) {
        return new ProcessOrderCreatedService(inventoryStockRepositoryPort, orderAuditRepositoryPort);
    }
}
