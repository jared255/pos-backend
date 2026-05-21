package com.fastfoodpos.infrastructure.config;

import com.fastfoodpos.ordering.application.service.ManageOrderService;
import com.fastfoodpos.ordering.domain.port.in.ManageOrderPort;
import com.fastfoodpos.ordering.domain.port.out.DomainEventPublisherPort;
import com.fastfoodpos.ordering.domain.port.out.OrderRepositoryPort;
import com.fastfoodpos.ordering.domain.port.out.TicketNumberPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderingUseCaseConfig {
    @Bean
    public ManageOrderPort manageOrderPort(OrderRepositoryPort orderRepositoryPort, TicketNumberPort ticketNumberPort, DomainEventPublisherPort domainEventPublisherPort) {
        return new ManageOrderService(orderRepositoryPort, ticketNumberPort, domainEventPublisherPort);
    }
}
