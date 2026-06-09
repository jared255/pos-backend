package com.fastfoodpos.inventory.infrastructure.persistence.jdbc;

import com.fastfoodpos.inventory.domain.port.out.InventoryStockRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JdbcInventoryStockRepositoryAdapter implements InventoryStockRepositoryPort {
    private static final String DECREMENT_STOCK_SQL = """
            UPDATE product
            SET stock = stock - ?,
                status = CASE
                    WHEN stock - ? <= 0 THEN 'AGOTADO'
                    ELSE status
                END
            WHERE id = ?
              AND deleted_at IS NULL
              AND status <> 'INACTIVO'
              AND stock IS NOT NULL
              AND stock >= ?
            """;
    private static final String EXISTS_PRODUCT_SQL = """
            SELECT EXISTS(
                SELECT 1
                FROM product
                WHERE id = ?
                  AND deleted_at IS NULL
            )
            """;
    private static final String GET_STOCK_SQL = """
            SELECT stock
            FROM product
            WHERE id = ?
              AND deleted_at IS NULL
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcInventoryStockRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean decrementStockIfAvailable(Integer productId, Integer quantity) {
        int updated = jdbcTemplate.update(DECREMENT_STOCK_SQL, quantity, quantity, productId, quantity);
        return updated > 0;
    }

    @Override
    public boolean existsById(Integer productId) {
        Boolean exists = jdbcTemplate.queryForObject(EXISTS_PRODUCT_SQL, Boolean.class, productId);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Optional<Integer> findCurrentStock(Integer productId) {
        return jdbcTemplate.query(GET_STOCK_SQL, rs -> {
            if (rs.next()) {
                int stock = rs.getInt("stock");
                if (rs.wasNull()) {
                    return Optional.<Integer>empty();
                }
                return Optional.of(stock);
            }
            return Optional.<Integer>empty();
        }, productId);
    }
}
