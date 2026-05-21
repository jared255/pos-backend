package com.fastfoodpos.ordering.infrastructure.persistence.jdbc;

import com.fastfoodpos.ordering.domain.port.out.TicketNumberPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class JdbcTicketNumberAdapter implements TicketNumberPort {
    private static final Logger logger = LoggerFactory.getLogger(JdbcTicketNumberAdapter.class);
    private static final String NEXT_TICKET = "SELECT nextval('orders_order_number_seq')";
    private final DataSource ds;

    public JdbcTicketNumberAdapter(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public Integer nextTicketNumber() {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(NEXT_TICKET); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo obtener el siguiente numero de ticket");
        } catch (SQLException e) {
            logger.error("Error obteniendo numero de ticket", e);
            throw new RuntimeException("Error obteniendo numero de ticket", e);
        }
    }
}
