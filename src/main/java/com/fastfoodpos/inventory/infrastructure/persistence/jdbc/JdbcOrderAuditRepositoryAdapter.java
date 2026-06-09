package com.fastfoodpos.inventory.infrastructure.persistence.jdbc;

import com.fastfoodpos.inventory.domain.model.OrderAuditEntry;
import com.fastfoodpos.inventory.domain.port.out.OrderAuditRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Repository
public class JdbcOrderAuditRepositoryAdapter implements OrderAuditRepositoryPort {
    private static final String INSERT_AUDIT_SQL = """
            INSERT INTO order_audit (order_id, order_number, user_id, event_type, result, message, occurred_at)
            VALUES (?,?,?,?,?,?,?)
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcOrderAuditRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void register(OrderAuditEntry entry) {
        jdbcTemplate.update(
                INSERT_AUDIT_SQL,
                entry.getOrderId(),
                entry.getOrderNumber(),
                entry.getUserId(),
                entry.getEventType(),
                entry.getResult(),
                entry.getMessage(),
                Timestamp.from(entry.getOccurredAt())
        );
    }
}
