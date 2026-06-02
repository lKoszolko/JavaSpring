package org.example.repositories.impl.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("jdbc")
@Repository
public class UserJdbcRepository implements UserRepository {

    @Override
    public User save(User user) {
        String sql = """
            INSERT INTO users (id, login, password, role) 
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET 
                login = EXCLUDED.login, 
                password = EXCLUDED.password, 
                role = EXCLUDED.role
            """;
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
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
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getString("id"),
                        rs.getString("login"),
                        rs.getString("password"),
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
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getString("id"),
                        rs.getString("login"),
                        rs.getString("password"),
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
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}