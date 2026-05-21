package com.fastfoodpos.ordering.infrastructure.persistence.jdbc;

import com.fastfoodpos.ordering.domain.model.Order;
import com.fastfoodpos.ordering.domain.model.OrderItem;
import com.fastfoodpos.ordering.domain.model.OrderStatus;
import com.fastfoodpos.ordering.domain.port.out.OrderRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JdbcOrderRepositoryAdapter implements OrderRepositoryPort {
    private static final Logger logger = LoggerFactory.getLogger(JdbcOrderRepositoryAdapter.class);
    private static final String INSERT_ORDER = "INSERT INTO orders (total, order_number, user_id, status_id) VALUES (?,?,?,?)";
    private static final String INSERT_ORDER_DETAIL = "INSERT INTO order_detail (order_id, product_id, quantity, price, subtotal) VALUES (?,?,?,?,?)";
    private static final String GET_ONE = """
            SELECT o.id, o.order_date, o.total, o.order_number, o.user_id, s.name AS status_name
            FROM orders o
            JOIN order_status s ON s.id = o.status_id
            WHERE o.id = ?
            """;
    private static final String GET_ORDER_ITEMS = """
            SELECT product_id, quantity, price, subtotal
            FROM order_detail
            WHERE order_id = ?
            ORDER BY product_id
            """;
    private static final String UPDATE_STATUS = "UPDATE orders SET status_id = ? WHERE id = ?";
    private static final String GET_STATUS_ID = "SELECT id FROM order_status WHERE UPPER(name) = ?";
    private static final String UPSERT_STATUS = """
            INSERT INTO order_status (name)
            VALUES (?)
            ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name
            RETURNING id
            """;

    private final DataSource ds;

    public JdbcOrderRepositoryAdapter(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public Integer insert(Order order) {
        Integer statusId = resolveStatusId(order.getStatus());
        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer orderId;
                try (PreparedStatement ps = conn.prepareStatement(INSERT_ORDER, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps.setBigDecimal(1, order.getTotal());
                    ps.setInt(2, order.getOrderNumber());
                    ps.setInt(3, order.getUserId());
                    ps.setInt(4, statusId);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No se pudo obtener el id generado para el pedido");
                        }
                        orderId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(INSERT_ORDER_DETAIL)) {
                    for (OrderItem item : order.getItems()) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, item.getProductId());
                        ps.setInt(3, item.getQuantity());
                        ps.setBigDecimal(4, item.getUnitPrice());
                        ps.setBigDecimal(5, item.getSubtotal());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return orderId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Error insertando pedido para usuario {}", order.getUserId(), e);
            throw new RuntimeException("Error insertando pedido", e);
        }
    }

    @Override
    public Optional<Order> findById(Integer id) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(GET_ONE)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Order order = mapOrder(rs);
                order.setItems(findItems(conn, id));
                return Optional.of(order);
            }
        } catch (SQLException e) {
            logger.error("Error buscando pedido con id {}", id, e);
            throw new RuntimeException("Error buscando pedido por id", e);
        }
    }

    @Override
    public List<Order> findByStatuses(List<OrderStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return List.of();
        }
        String placeholders = statuses.stream().map(status -> "?").collect(Collectors.joining(","));
        String sql = """
                SELECT o.id, o.order_date, o.total, o.order_number, o.user_id, s.name AS status_name
                FROM orders o
                JOIN order_status s ON s.id = o.status_id
                WHERE UPPER(s.name) IN (%s)
                ORDER BY o.id DESC
                """.formatted(placeholders);
        List<Order> orders = new ArrayList<>();
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < statuses.size(); i++) {
                ps.setString(i + 1, statuses.get(i).name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrder(rs);
                    order.setItems(findItems(conn, order.getId()));
                    orders.add(order);
                }
            }
            return orders;
        } catch (SQLException e) {
            logger.error("Error listando pedidos por estado {}", statuses, e);
            throw new RuntimeException("Error listando pedidos por estado", e);
        }
    }

    @Override
    public void updateStatus(Integer id, OrderStatus status) {
        Integer statusId = resolveStatusId(status);
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {
            ps.setInt(1, statusId);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error actualizando estado de pedido {} a {}", id, status, e);
            throw new RuntimeException("Error actualizando estado de pedido", e);
        }
    }

    @Override
    public Integer resolveStatusId(OrderStatus status) {
        String normalizedStatus = status.name();
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement select = conn.prepareStatement(GET_STATUS_ID)) {
                select.setString(1, normalizedStatus);
                try (ResultSet rs = select.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
            try (PreparedStatement insert = conn.prepareStatement(UPSERT_STATUS)) {
                insert.setString(1, normalizedStatus);
                try (ResultSet rs = insert.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
            throw new SQLException("No se pudo resolver el id del estado " + normalizedStatus);
        } catch (SQLException e) {
            logger.error("Error resolviendo estado {}", normalizedStatus, e);
            throw new RuntimeException("Error resolviendo estado de pedido", e);
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Timestamp orderDate = rs.getTimestamp("order_date");
        return new Order(
                rs.getInt("id"),
                rs.getInt("order_number"),
                rs.getInt("user_id"),
                rs.getBigDecimal("total"),
                OrderStatus.valueOf(rs.getString("status_name").trim().toUpperCase()),
                orderDate == null ? null : orderDate.toLocalDateTime(),
                List.of()
        );
    }

    private List<OrderItem> findItems(Connection conn, Integer orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(GET_ORDER_ITEMS)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderItem(
                            rs.getInt("product_id"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("price"),
                            rs.getBigDecimal("subtotal")
                    ));
                }
            }
        }
        return items;
    }
}
