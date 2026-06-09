package com.fastfoodpos.inventory.domain.port.out;

import com.fastfoodpos.inventory.domain.model.OrderAuditEntry;

public interface OrderAuditRepositoryPort {
    void register(OrderAuditEntry entry);
}
