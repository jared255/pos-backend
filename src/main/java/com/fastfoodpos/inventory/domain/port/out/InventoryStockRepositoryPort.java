package com.fastfoodpos.inventory.domain.port.out;

import java.util.Optional;

public interface InventoryStockRepositoryPort {
    boolean decrementStockIfAvailable(Integer productId, Integer quantity);

    boolean existsById(Integer productId);

    Optional<Integer> findCurrentStock(Integer productId);
}
