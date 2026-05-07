package com.fastfoodpos.infrastructure.persistence.jdbc;

import com.fastfoodpos.domain.model.Product;
import com.fastfoodpos.domain.port.out.ProductRepositoryPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcProductRepositoryAdapter implements ProductRepositoryPort {


    private final DataSource ds;
    private static final String INSERT = "INSERT INTO product (name, description, price, stock, status, category_id) VALUES (?,?,?,?,?,?)";
    private static final String UPDATE = "UPDATE product SET name=?, description=?, price=?, stock=?, status=?, category_id=? WHERE id=?";
    private static final String DELETE = "UPDATE product SET deleted_at=CURRENT_TIMESTAMP, status='INACTIVO' WHERE id=? AND deleted_at IS NULL";
    private static final String GETONE = "SELECT id, name, description, price, stock, status, category_id FROM product WHERE id=? AND deleted_at IS NULL";
    private static final String GETALL = "SELECT id, name, description, price, stock, status, category_id FROM product WHERE deleted_at IS NULL ORDER BY id";

    // instancia de logger
    private static final Logger logger = LoggerFactory.getLogger(JdbcProductRepositoryAdapter.class);

    public JdbcProductRepositoryAdapter(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<Product> findAll() {
        List<Product> result = new ArrayList<>();
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(GETALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener los productos", e);
            throw new RuntimeException("Error al obtener los productos ", e);
        }
        return result;
    }

    @Override
    public Optional<Product> findById(Integer id) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(GETONE)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapProduct(rs));
                } else {
                    logger.info("Producto no encontrado con id {}", id);
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            logger.error("Error al buscar producto por id {}", id, e);
            throw new RuntimeException("Error al buscar producto por id: " + id, e);
        }
    }

    @Override
    public Integer save(Product product) {
        if (product.getId() == null) {
            // Insert
            try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, product.getName());
                ps.setString(2, product.getDescription());
                ps.setBigDecimal(3, product.getPrice());
                ps.setInt(4, product.getStock());
                ps.setString(5, product.getStatus().name());
                ps.setInt(6, product.getCategoryId());

                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        Integer newId = keys.getInt(1);
                        logger.info("Producto insertado con id {}", newId);
                        return newId;
                    }
                }
                return null;
            } catch (SQLException e) {
                logger.error("Error insertando producto {}", product.getName(), e);
                throw new RuntimeException("Error insertando producto ", e);
            }
        } else {
            // Update
            try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE)) {
                ps.setString(1, product.getName());
                ps.setString(2, product.getDescription());
                ps.setBigDecimal(3, product.getPrice());
                ps.setInt(4, product.getStock());
                ps.setString(5, product.getStatus().name());
                ps.setInt(6, product.getCategoryId());
                ps.setInt(7, product.getId());

                ps.executeUpdate();
                logger.info("Producto actualizado con id {} ", product.getId());
                return product.getId();
            } catch (SQLException e) {
                logger.error("Error actualizando producto con id {}", product.getId(), e);
                throw new RuntimeException("Error actualizando producto", e);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.info("Producto eliminado con id {}", id);
        } catch (SQLException e) {
            logger.error("Error eliminando producto con id {}", id, e);
            throw new RuntimeException("Error eliminando producto con id: " + id, e);
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getInt("stock"),
                Product.ProductStatus.valueOf(rs.getString("status").trim().toUpperCase()),
                rs.getInt("category_id")
        );
    }
}
