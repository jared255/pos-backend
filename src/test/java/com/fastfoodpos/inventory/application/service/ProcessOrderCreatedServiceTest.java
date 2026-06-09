package com.fastfoodpos.inventory.application.service;

import com.fastfoodpos.inventory.domain.exception.InsufficientStockException;
import com.fastfoodpos.inventory.domain.model.OrderAuditEntry;
import com.fastfoodpos.inventory.domain.model.OrderCreatedStockAdjustment;
import com.fastfoodpos.inventory.domain.model.OrderCreatedStockItem;
import com.fastfoodpos.inventory.domain.port.out.InventoryStockRepositoryPort;
import com.fastfoodpos.inventory.domain.port.out.OrderAuditRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessOrderCreatedServiceTest {
    private InventoryStockRepositoryPort inventoryStockRepositoryPort;
    private OrderAuditRepositoryPort orderAuditRepositoryPort;
    private ProcessOrderCreatedService service;

    @BeforeEach
    void setUp() {
        inventoryStockRepositoryPort = mock(InventoryStockRepositoryPort.class);
        orderAuditRepositoryPort = mock(OrderAuditRepositoryPort.class);
        service = new ProcessOrderCreatedService(inventoryStockRepositoryPort, orderAuditRepositoryPort);
    }

    @Test
    void processDecrementsStockAndRegistersSuccessAudit() {
        OrderCreatedStockAdjustment adjustment = new OrderCreatedStockAdjustment(
                10,
                2001,
                3,
                List.of(
                        new OrderCreatedStockItem(5, 1),
                        new OrderCreatedStockItem(5, 2),
                        new OrderCreatedStockItem(6, 1)
                ),
                Instant.parse("2026-05-22T12:00:00Z")
        );
        when(inventoryStockRepositoryPort.decrementStockIfAvailable(5, 3)).thenReturn(true);
        when(inventoryStockRepositoryPort.decrementStockIfAvailable(6, 1)).thenReturn(true);

        service.process(adjustment);

        verify(inventoryStockRepositoryPort).decrementStockIfAvailable(5, 3);
        verify(inventoryStockRepositoryPort).decrementStockIfAvailable(6, 1);

        ArgumentCaptor<OrderAuditEntry> captor = ArgumentCaptor.forClass(OrderAuditEntry.class);
        verify(orderAuditRepositoryPort).register(captor.capture());
        assertEquals("SUCCESS", captor.getValue().getResult());
    }

    @Test
    void processRegistersFailureAuditAndThrowsWhenStockIsInsufficient() {
        OrderCreatedStockAdjustment adjustment = new OrderCreatedStockAdjustment(
                11,
                2002,
                4,
                List.of(new OrderCreatedStockItem(8, 5)),
                Instant.parse("2026-05-22T12:00:00Z")
        );
        when(inventoryStockRepositoryPort.decrementStockIfAvailable(8, 5)).thenReturn(false);
        when(inventoryStockRepositoryPort.existsById(8)).thenReturn(true);
        when(inventoryStockRepositoryPort.findCurrentStock(8)).thenReturn(Optional.of(1));

        assertThrows(InsufficientStockException.class, () -> service.process(adjustment));

        ArgumentCaptor<OrderAuditEntry> captor = ArgumentCaptor.forClass(OrderAuditEntry.class);
        verify(orderAuditRepositoryPort).register(captor.capture());
        assertEquals("FAILED", captor.getValue().getResult());
    }
}
