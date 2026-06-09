package org.example.repositories.impl.jdbc;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("jdbc")
@Repository
public class RentalJdbcRepository implements RentalRepository {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final DataSource dataSource;

    public RentalJdbcRepository(VehicleRepository vehicleRepository, UserRepository userRepository, DataSource dataSource) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.dataSource = dataSource;
    }

    @Override
    public Rental save(Rental rental) {
        String sql = """
            INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET 
                return_date = EXCLUDED.return_date
            """;

        Connection connection = DataSourceUtils.getConnection(dataSource); // <-- Łączymy z Neonem!
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, rental.getId());
            pstmt.setString(2, rental.getVehicle().getId());
            pstmt.setString(3, rental.getUser().getId());
            pstmt.setString(4, rental.getRentDateTime());

            if (rental.getReturnDateTime() == null || rental.getReturnDateTime().isBlank()) {
                pstmt.setNull(5, Types.VARCHAR);
            } else {
                pstmt.setString(5, rental.getReturnDateTime());
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu wypożyczenia: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return rental;
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT * FROM rental WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRental(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, vehicleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRental(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
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