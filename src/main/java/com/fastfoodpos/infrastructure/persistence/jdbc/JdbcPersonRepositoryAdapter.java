package com.fastfoodpos.infrastructure.persistence.jdbc;

import com.fastfoodpos.domain.model.Person;
import com.fastfoodpos.domain.port.out.PersonRepositoryPort;
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
public class JdbcPersonRepositoryAdapter implements PersonRepositoryPort {
    private static final Logger logger = LoggerFactory.getLogger(JdbcPersonRepositoryAdapter.class);
    private static final String INSERT = "INSERT INTO person (first_name, paternal_last_name, maternal_last_name, phone) VALUES (?,?,?,?)";
    private static final String UPDATE = "UPDATE person SET first_name=?, paternal_last_name=?, maternal_last_name=?, phone=? WHERE id=? AND deleted_at IS NULL";
    private static final String DELETE = "UPDATE person SET deleted_at=CURRENT_TIMESTAMP WHERE id=? AND deleted_at IS NULL";
    private static final String GETONE = "SELECT id, first_name, paternal_last_name, maternal_last_name, phone FROM person WHERE id=? AND deleted_at IS NULL";
    private static final String GETALL = "SELECT id, first_name, paternal_last_name, maternal_last_name, phone FROM person WHERE deleted_at IS NULL ORDER BY id";

    private final DataSource ds;

    public JdbcPersonRepositoryAdapter(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<Person> findAll() {
        List<Person> result = new ArrayList<>();
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(GETALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapPerson(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener personas", e);
            throw new RuntimeException("Error al obtener personas", e);
        }
        return result;
    }

    @Override
    public Optional<Person> findById(Integer id) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(GETONE)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPerson(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error al buscar persona por id {}", id, e);
            throw new RuntimeException("Error al buscar persona por id: " + id, e);
        }
    }

    @Override
    public Integer save(Person person) {
        if (person.getId() == null) {
            return insert(person);
        }
        return update(person);
    }

    @Override
    public void deleteById(Integer id) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.info("Persona eliminada logicamente con id {}", id);
        } catch (SQLException e) {
            logger.error("Error eliminando persona con id {}", id, e);
            throw new RuntimeException("Error eliminando persona con id: " + id, e);
        }
    }

    private Integer insert(Person person) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setPersonFields(ps, person);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error insertando persona {}", person.getFirstName(), e);
            throw new RuntimeException("Error insertando persona", e);
        }
    }

    private Integer update(Person person) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            setPersonFields(ps, person);
            ps.setInt(5, person.getId());
            ps.executeUpdate();
            return person.getId();
        } catch (SQLException e) {
            logger.error("Error actualizando persona con id {}", person.getId(), e);
            throw new RuntimeException("Error actualizando persona", e);
        }
    }

    private void setPersonFields(PreparedStatement ps, Person person) throws SQLException {
        ps.setString(1, person.getFirstName());
        ps.setString(2, person.getPaternalLastName());
        ps.setString(3, person.getMaternalLastName());
        ps.setString(4, person.getPhone());
    }

    private Person mapPerson(ResultSet rs) throws SQLException {
        return new Person(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("paternal_last_name"),
                rs.getString("maternal_last_name"),
                rs.getString("phone")
        );
    }
}
