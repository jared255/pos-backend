package com.fastfoodpos.inventory.application.service;

import com.fastfoodpos.inventory.domain.exception.InsufficientStockException;
import com.fastfoodpos.inventory.domain.exception.InventoryProductNotFoundException;
import com.fastfoodpos.inventory.domain.model.OrderAuditEntry;
import com.fastfoodpos.inventory.domain.model.OrderCreatedStockAdjustment;
import com.fastfoodpos.inventory.domain.model.OrderCreatedStockItem;
import com.fastfoodpos.inventory.domain.port.in.ProcessOrderCreatedPort;
import com.fastfoodpos.inventory.domain.port.out.InventoryStockRepositoryPort;
import com.fastfoodpos.inventory.domain.port.out.OrderAuditRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessOrderCreatedService implements ProcessOrderCreatedPort {
    private static final String EVENT_TYPE = "ORDER_CREATED";
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_FAILED = "FAILED";

    private final InventoryStockRepositoryPort inventoryStockRepositoryPort;
    private final OrderAuditRepositoryPort orderAuditRepositoryPort;

    public ProcessOrderCreatedService(InventoryStockRepositoryPort inventoryStockRepositoryPort,
                                      OrderAuditRepositoryPort orderAuditRepositoryPort) {
        this.inventoryStockRepositoryPort = inventoryStockRepositoryPort;
        this.orderAuditRepositoryPort = orderAuditRepositoryPort;
    }

    @Override
    @Transactional
    public void process(OrderCreatedStockAdjustment adjustment) {
        validateAdjustment(adjustment);

        try {
            Map<Integer, Integer> quantitiesByProduct = aggregateItems(adjustment);
            for (Map.Entry<Integer, Integer> entry : quantitiesByProduct.entrySet()) {
                Integer productId = entry.getKey();
                Integer requestedQuantity = entry.getValue();
                boolean updated = inventoryStockRepositoryPort.decrementStockIfAvailable(productId, requestedQuantity);
                if (!updated) {
                    if (!inventoryStockRepositoryPort.existsById(productId)) {
                        throw new InventoryProductNotFoundException(productId);
                    }
                    Integer currentStock = inventoryStockRepositoryPort.findCurrentStock(productId).orElse(0);
                    throw new InsufficientStockException(productId, requestedQuantity, currentStock);
                }
            }

            orderAuditRepositoryPort.register(new OrderAuditEntry(
                    adjustment.getOrderId(),
                    adjustment.getOrderNumber(),
                    adjustment.getUserId(),
                    EVENT_TYPE,
                    RESULT_SUCCESS,
                    "Inventario descontado correctamente",
                    resolveOccurredAt(adjustment)
            ));
        } catch (RuntimeException exception) {
            orderAuditRepositoryPort.register(new OrderAuditEntry(
                    adjustment.getOrderId(),
                    adjustment.getOrderNumber(),
                    adjustment.getUserId(),
                    EVENT_TYPE,
                    RESULT_FAILED,
                    exception.getMessage(),
                    resolveOccurredAt(adjustment)
            ));
            throw exception;
        }
    }

    private void validateAdjustment(OrderCreatedStockAdjustment adjustment) {
        if (adjustment == null) {
            throw new IllegalArgumentException("El ajuste de inventario no puede ser nulo");
        }
        if (adjustment.getItems() == null || adjustment.getItems().isEmpty()) {
            throw new IllegalArgumentException("El evento de pedido creado debe incluir items");
        }
        for (OrderCreatedStockItem item : adjustment.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Cada item debe incluir productId");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("La cantidad por item debe ser mayor a cero");
            }
        }
    }

    private Map<Integer, Integer> aggregateItems(OrderCreatedStockAdjustment adjustment) {
        Map<Integer, Integer> quantitiesByProduct = new LinkedHashMap<>();
        for (OrderCreatedStockItem item : adjustment.getItems()) {
            quantitiesByProduct.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
        return quantitiesByProduct;
    }

    private Instant resolveOccurredAt(OrderCreatedStockAdjustment adjustment) {
        return adjustment.getOccurredAt() == null ? Instant.now() : adjustment.getOccurredAt();
    }
}
