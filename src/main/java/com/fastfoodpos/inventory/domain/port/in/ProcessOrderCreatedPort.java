package com.fastfoodpos.inventory.domain.port.in;

import com.fastfoodpos.inventory.domain.model.OrderCreatedStockAdjustment;

public interface ProcessOrderCreatedPort {
    void process(OrderCreatedStockAdjustment adjustment);
}
