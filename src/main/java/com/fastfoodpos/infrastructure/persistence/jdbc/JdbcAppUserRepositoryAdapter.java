package com.fastfoodpos.infrastructure.persistence.jdbc;

import com.fastfoodpos.domain.model.AppUser;
import com.fastfoodpos.domain.model.Person;
import com.fastfoodpos.domain.model.Role;
import com.fastfoodpos.domain.port.out.AppUserRepositoryPort;
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
public class JdbcAppUserRepositoryAdapter implements AppUserRepositoryPort {
    private static final Logger logger = LoggerFactory.getLogger(JdbcAppUserRepositoryAdapter.class);
    private static final String INSERT = "INSERT INTO app_user (username, password, person_id) VALUES (?,?,?)";
    private static final String UPDATE_WITH_PASSWORD = "UPDATE app_user SET username=?, password=?, person_id=? WHERE id=? AND deleted_at IS NULL";
    private static final String UPDATE_WITHOUT_PASSWORD = "UPDATE app_user SET username=?, person_id=? WHERE id=? AND deleted_at IS NULL";
    private static final String DELETE = "UPDATE app_user SET deleted_at=CURRENT_TIMESTAMP WHERE id=? AND deleted_at IS NULL";
    private static final String GETONE = """
            SELECT u.id, u.username, u.password, u.person_id,
                   p.first_name, p.paternal_last_name, p.maternal_last_name, p.phone
            FROM app_user u
            JOIN person p ON p.id = u.person_id
            WHERE u.id=? AND u.deleted_at IS NULL AND p.deleted_at IS NULL
            """;
    private static final String GETALL = """
            SELECT u.id, u.username, u.password, u.person_id,
                   p.first_name, p.paternal_last_name, p.maternal_last_name, p.phone
            FROM app_user u
            JOIN person p ON p.id = u.person_id
            WHERE u.deleted_at IS NULL AND p.deleted_at IS NULL
            ORDER BY u.id
            """;
    private static final String DELETE_USER_ROLES = "DELETE FROM user_role WHERE user_id=?";
    private static final String INSERT_USER_ROLE = "INSERT INTO user_role (user_id, role_id) VALUES (?,?)";
    private static final String GET_USER_ROLES = """
            SELECT r.id, r.name
            FROM user_role ur
            JOIN role r ON r.id = ur.role_id
            WHERE ur.user_id=? AND r.deleted_at IS NULL
            ORDER BY r.id
            """;

    private final DataSource ds;

    public JdbcAppUserRepositoryAdapter(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<AppUser> findAll() {
        List<AppUser> result = new ArrayList<>();
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(GETALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapAppUser(conn, rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener usuarios", e);
            throw new RuntimeException("Error al obtener usuarios", e);
        }
        return result;
    }

    @Override
    public Optional<AppUser> findById(Integer id) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(GETONE)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAppUser(conn, rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error al buscar usuario por id {}", id, e);
            throw new RuntimeException("Error al buscar usuario por id: " + id, e);
        }
    }

    @Override
    public Integer save(AppUser appUser) {
        if (appUser.getId() == null) {
            return insert(appUser);
        }
        return update(appUser);
    }

    @Override
    public void deleteById(Integer id) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.info("Usuario eliminado logicamente con id {}", id);
        } catch (SQLException e) {
            logger.error("Error eliminando usuario con id {}", id, e);
            throw new RuntimeException("Error eliminando usuario con id: " + id, e);
        }
    }

    private Integer insert(AppUser appUser) {
        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, appUser.getUsername());
                ps.setString(2, appUser.getPassword());
                ps.setInt(3, appUser.getPersonId());
                ps.executeUpdate();

                Integer newId = null;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        newId = keys.getInt(1);
                    }
                }
                replaceRoles(conn, newId, appUser.getRoles());
                conn.commit();
                return newId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Error insertando usuario {}", appUser.getUsername(), e);
            throw new RuntimeException("Error insertando usuario", e);
        }
    }

    private Integer update(AppUser appUser) {
        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(hasPassword(appUser) ? UPDATE_WITH_PASSWORD : UPDATE_WITHOUT_PASSWORD)) {
                ps.setString(1, appUser.getUsername());
                if (hasPassword(appUser)) {
                    ps.setString(2, appUser.getPassword());
                    ps.setInt(3, appUser.getPersonId());
                    ps.setInt(4, appUser.getId());
                } else {
                    ps.setInt(2, appUser.getPersonId());
                    ps.setInt(3, appUser.getId());
                }
                ps.executeUpdate();
                replaceRoles(conn, appUser.getId(), appUser.getRoles());
                conn.commit();
                return appUser.getId();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Error actualizando usuario con id {}", appUser.getId(), e);
            throw new RuntimeException("Error actualizando usuario", e);
        }
    }

    private boolean hasPassword(AppUser appUser) {
        return appUser.getPassword() != null && !appUser.getPassword().isBlank();
    }

    private void replaceRoles(Connection conn, Integer userId, List<Role> roles) throws SQLException {
        try (PreparedStatement delete = conn.prepareStatement(DELETE_USER_ROLES)) {
            delete.setInt(1, userId);
            delete.executeUpdate();
        }

        try (PreparedStatement insert = conn.prepareStatement(INSERT_USER_ROLE)) {
            for (Role role : roles) {
                insert.setInt(1, userId);
                insert.setInt(2, role.getId());
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private AppUser mapAppUser(Connection conn, ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Person person = new Person(
                rs.getInt("person_id"),
                rs.getString("first_name"),
                rs.getString("paternal_last_name"),
                rs.getString("maternal_last_name"),
                rs.getString("phone")
        );
        return new AppUser(
                id,
                rs.getString("username"),
                rs.getString("password"),
                rs.getInt("person_id"),
                person,
                findRoles(conn, id)
        );
    }

    private List<Role> findRoles(Connection conn, Integer userId) throws SQLException {
        List<Role> roles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(GET_USER_ROLES)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(new Role(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return roles;
    }
}
