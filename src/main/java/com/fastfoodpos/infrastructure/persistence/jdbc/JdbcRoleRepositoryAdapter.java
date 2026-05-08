package com.fastfoodpos.infrastructure.persistence.jdbc;

import com.fastfoodpos.domain.model.Role;
import com.fastfoodpos.domain.port.out.RoleRepositoryPort;
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
public class JdbcRoleRepositoryAdapter implements RoleRepositoryPort {
    private static final Logger logger = LoggerFactory.getLogger(JdbcRoleRepositoryAdapter.class);
    private static final String INSERT = "INSERT INTO role (name) VALUES (?)";
    private static final String UPDATE = "UPDATE role SET name=? WHERE id=? AND deleted_at IS NULL";
    private static final String DELETE = "UPDATE role SET deleted_at=CURRENT_TIMESTAMP WHERE id=? AND deleted_at IS NULL";
    private static final String GETONE = "SELECT id, name FROM role WHERE id=? AND deleted_at IS NULL";
    private static final String GETALL = "SELECT id, name FROM role WHERE deleted_at IS NULL ORDER BY id";

    private final DataSource ds;

    public JdbcRoleRepositoryAdapter(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<Role> findAll() {
        List<Role> result = new ArrayList<>();
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(GETALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRole(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener roles", e);
            throw new RuntimeException("Error al obtener roles", e);
        }
        return result;
    }

    @Override
    public Optional<Role> findById(Integer id) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(GETONE)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRole(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error al buscar rol por id {}", id, e);
            throw new RuntimeException("Error al buscar rol por id: " + id, e);
        }
    }

    @Override
    public Integer save(Role role) {
        if (role.getId() == null) {
            return insert(role);
        }
        return update(role);
    }

    @Override
    public void deleteById(Integer id) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.info("Rol eliminado logicamente con id {}", id);
        } catch (SQLException e) {
            logger.error("Error eliminando rol con id {}", id, e);
            throw new RuntimeException("Error eliminando rol con id: " + id, e);
        }
    }

    private Integer insert(Role role) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, normalizeName(role.getName()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error insertando rol {}", role.getName(), e);
            throw new RuntimeException("Error insertando rol", e);
        }
    }

    private Integer update(Role role) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, normalizeName(role.getName()));
            ps.setInt(2, role.getId());
            ps.executeUpdate();
            return role.getId();
        } catch (SQLException e) {
            logger.error("Error actualizando rol con id {}", role.getId(), e);
            throw new RuntimeException("Error actualizando rol", e);
        }
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim().toUpperCase();
    }

    private Role mapRole(ResultSet rs) throws SQLException {
        return new Role(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
