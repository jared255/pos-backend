package com.fastfoodpos.ordering.domain.port.out;

public interface DomainEventPublisherPort {
    void publish(Object domainEvent);
}
