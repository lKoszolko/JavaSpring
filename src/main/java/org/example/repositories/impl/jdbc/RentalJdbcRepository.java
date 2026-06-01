package org.example.repositories.impl.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("jdbc")
@Repository
public class RentalJdbcRepository implements RentalRepository {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public RentalJdbcRepository(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Rental save(Rental rental) {
        String sql = """
            INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) 
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET 
                return_date = EXCLUDED.return_date
            """;
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rental.getId());
            pstmt.setString(2, rental.getVehicle().getId());
            pstmt.setString(3, rental.getUser().getId());
            pstmt.setString(4, rental.getRentDateTime());
            pstmt.setString(5, rental.getReturnDateTime());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu wypożyczenia: " + e.getMessage(), e);
        }
        return rental;
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT * FROM rental WHERE id = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private Rental mapResultSetToRental(ResultSet rs) throws SQLException {
        String vehicleId = rs.getString("vehicle_id");
        String userId = rs.getString("user_id");

        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        return new Rental(
                rs.getString("id"),
                vehicle,
                user,
                rs.getString("rent_date"),
                rs.getString("return_date")
        );
    }
}