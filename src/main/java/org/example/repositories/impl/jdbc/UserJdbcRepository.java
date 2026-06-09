package org.example.repositories.impl.jdbc;

import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource; // Zwróć uwagę na ten import!
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("jdbc")
@Repository
public class UserJdbcRepository implements UserRepository {

    private final DataSource dataSource;

    public UserJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User save(User user) {
        String sql = """
            INSERT INTO users (id, login, password_hash, role) 
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET 
                login = EXCLUDED.login, 
                password_hash = EXCLUDED.password_hash, 
                role = EXCLUDED.role
            """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getLogin());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu użytkownika do bazy: " + e.getMessage(), e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return findByColumn("id", id);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return findByColumn("login", login);
    }

    private Optional<User> findByColumn(String columnName, String value) {
        String sql = "SELECT * FROM users WHERE " + columnName + " = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getString("id"),
                        rs.getString("login"),
                        rs.getString("password_hash"),
                        Role.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getString("id"),
                        rs.getString("login"),
                        rs.getString("password_hash"),
                        Role.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public void deleteById(String id) {
        deleteByColumn("id", id);
    }

    @Override
    public void deleteByLogin(String login) {
        deleteByColumn("login", login);
    }

    private void deleteByColumn(String columnName, String value) {
        String sql = "DELETE FROM users WHERE " + columnName + " = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}