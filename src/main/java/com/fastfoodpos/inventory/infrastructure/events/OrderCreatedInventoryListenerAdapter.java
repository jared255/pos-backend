package com.fastfoodpos.inventory.infrastructure.events;

import com.fastfoodpos.inventory.domain.model.OrderCreatedStockAdjustment;
import com.fastfoodpos.inventory.domain.model.OrderCreatedStockItem;
import com.fastfoodpos.inventory.domain.port.in.ProcessOrderCreatedPort;
import com.fastfoodpos.ordering.domain.event.OrderCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderCreatedInventoryListenerAdapter {
    private final ProcessOrderCreatedPort processOrderCreatedPort;

    public OrderCreatedInventoryListenerAdapter(ProcessOrderCreatedPort processOrderCreatedPort) {
        this.processOrderCreatedPort = processOrderCreatedPort;
    }

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        List<OrderCreatedStockItem> items = event.getItems()
                .stream()
                .map(item -> new OrderCreatedStockItem(item.getProductId(), item.getQuantity()))
                .toList();

        processOrderCreatedPort.process(new OrderCreatedStockAdjustment(
                event.getOrderId(),
                event.getOrderNumber(),
                event.getUserId(),
                items,
                event.getOccurredAt()
        ));
    }
}
