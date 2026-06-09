package com.fastfoodpos.ordering.application.service;

import com.fastfoodpos.ordering.domain.event.OrderCreatedEvent;
import com.fastfoodpos.ordering.domain.event.OrderDeliveredEvent;
import com.fastfoodpos.ordering.domain.event.OrderReadyEvent;
import com.fastfoodpos.ordering.domain.exception.InvalidOrderStatusTransitionException;
import com.fastfoodpos.ordering.domain.exception.OrderNotFoundException;
import com.fastfoodpos.ordering.domain.model.Order;
import com.fastfoodpos.ordering.domain.model.OrderItem;
import com.fastfoodpos.ordering.domain.model.OrderStatus;
import com.fastfoodpos.ordering.domain.port.in.ManageOrderPort;
import com.fastfoodpos.ordering.domain.port.out.DomainEventPublisherPort;
import com.fastfoodpos.ordering.domain.port.out.OrderRepositoryPort;
import com.fastfoodpos.ordering.domain.port.out.TicketNumberPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ManageOrderService implements ManageOrderPort {
    private static final Logger logger = LoggerFactory.getLogger(ManageOrderService.class);
    private final OrderRepositoryPort repository;
    private final TicketNumberPort ticketNumberPort;
    private final DomainEventPublisherPort domainEventPublisherPort;

    public ManageOrderService(OrderRepositoryPort repository, TicketNumberPort ticketNumberPort, DomainEventPublisherPort domainEventPublisherPort) {
        this.repository = repository;
        this.ticketNumberPort = ticketNumberPort;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    @Transactional
    public Integer createOrder(Order order) {
        logger.info("Creando pedido para usuario {}", order.getUserId());
        validateOrder(order);
        normalizeTotals(order);
        order.setOrderNumber(ticketNumberPort.nextTicketNumber());
        order.setStatus(OrderStatus.PREPARING);
        Integer orderId = repository.insert(order);
        List<OrderCreatedEvent.Item> eventItems = order.getItems()
                .stream()
                .map(item -> new OrderCreatedEvent.Item(item.getProductId(), item.getQuantity()))
                .toList();
        domainEventPublisherPort.publish(new OrderCreatedEvent(orderId, order.getOrderNumber(), order.getUserId(), eventItems));
        return orderId;
    }

    @Override
    public Optional<Order> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Order> findByStatuses(List<OrderStatus> statuses) {
        return repository.findByStatuses(statuses);
    }

    @Override
    public void changeStatus(Integer id, OrderStatus newStatus) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == newStatus) {
            return;
        }
        if (!isTransitionAllowed(currentStatus, newStatus)) {
            throw new InvalidOrderStatusTransitionException(currentStatus, newStatus);
        }
        repository.updateStatus(id, newStatus);
        if (newStatus == OrderStatus.READY) {
            domainEventPublisherPort.publish(new OrderReadyEvent(id, order.getOrderNumber()));
        } else if (newStatus == OrderStatus.DELIVERED) {
            domainEventPublisherPort.publish(new OrderDeliveredEvent(id, order.getOrderNumber()));
        }
    }

    private void validateOrder(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe incluir al menos un item");
        }
        for (OrderItem item : order.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("La cantidad de cada item debe ser mayor a cero");
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El precio de cada item no puede ser negativo");
            }
        }
    }

    private void normalizeTotals(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setSubtotal(subtotal);
            total = total.add(subtotal);
        }
        order.setTotal(total);
    }

    private boolean isTransitionAllowed(OrderStatus currentStatus, OrderStatus targetStatus) {
        return (currentStatus == OrderStatus.PREPARING && targetStatus == OrderStatus.READY)
                || (currentStatus == OrderStatus.READY && targetStatus == OrderStatus.DELIVERED);
    }
}
