package com.fastfoodpos.ordering.domain.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderCreatedEventTest {

    @Test
    void keepsOrderDataAndExplicitOccurrenceTime() {
        Instant occurredAt = Instant.parse("2026-05-22T12:00:00Z");

        OrderCreatedEvent event = new OrderCreatedEvent(
                10,
                2001,
                3,
                List.of(new OrderCreatedEvent.Item(5, 2)),
                occurredAt
        );

        assertEquals(10, event.getOrderId());
        assertEquals(2001, event.getOrderNumber());
        assertEquals(3, event.getUserId());
        assertEquals(occurredAt, event.getOccurredAt());
        assertEquals(5, event.getItems().get(0).getProductId());
        assertEquals(2, event.getItems().get(0).getQuantity());
    }

    @Test
    void defensivelyCopiesItems() {
        List<OrderCreatedEvent.Item> items = new ArrayList<>();
        items.add(new OrderCreatedEvent.Item(5, 2));

        OrderCreatedEvent event = new OrderCreatedEvent(10, 2001, 3, items);
        items.add(new OrderCreatedEvent.Item(6, 1));

        assertEquals(1, event.getItems().size());
        assertThrows(UnsupportedOperationException.class,
                () -> event.getItems().add(new OrderCreatedEvent.Item(7, 1)));
    }

    @Test
    void acceptsNullItemsAsEmptyListAndResolvesNullOccurrenceTime() {
        OrderCreatedEvent event = new OrderCreatedEvent(10, 2001, 3, null, null);

        assertTrue(event.getItems().isEmpty());
        assertNotNull(event.getOccurredAt());
    }

    @Test
    void keepsLegacyConstructorAvailableWithoutItems() {
        OrderCreatedEvent event = new OrderCreatedEvent(10, 2001, 3);

        assertEquals(10, event.getOrderId());
        assertTrue(event.getItems().isEmpty());
        assertNotNull(event.getOccurredAt());
    }

    @Test
    void rejectsNullItemInsideItems() {
        List<OrderCreatedEvent.Item> items = new ArrayList<>();
        items.add(null);

        assertThrows(NullPointerException.class,
                () -> new OrderCreatedEvent(10, 2001, 3, items));
    }
}
