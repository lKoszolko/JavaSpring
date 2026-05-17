package org.example.repositories.impl.jdbc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.db.JdbcConnectionManager;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Profile("jdbc")
@Repository

public class VehicleJdbcRepository implements VehicleRepository {
    private final Gson gson = new Gson();

    @Override
    public Vehicle save(Vehicle vehicle) {
        String sql = """
            INSERT INTO vehicle (id, brand, model, year, plate, price, category, additional_attributes) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)
            ON CONFLICT (id) DO UPDATE SET 
                brand = EXCLUDED.brand, model = EXCLUDED.model, year = EXCLUDED.year, 
                plate = EXCLUDED.plate, price = EXCLUDED.price, category = EXCLUDED.category, 
                additional_attributes = EXCLUDED.additional_attributes
            """;
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getId());
            pstmt.setString(2, vehicle.getBrand());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getYear());
            pstmt.setString(5, vehicle.getPlate());
            pstmt.setDouble(6, vehicle.getPrice());
            pstmt.setString(7, vehicle.getCategory());

            String attributesJson = gson.toJson(vehicle.getAttributes());
            pstmt.setString(8, attributesJson);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu pojazdu: " + e.getMessage(), e);
        }
        return vehicle;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT * FROM vehicle WHERE id = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicle";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return vehicles;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        String attributesJson = rs.getString("additional_attributes");
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> attributes = gson.fromJson(attributesJson, type);

        return Vehicle.builder()
                .id(rs.getString("id"))
                .brand(rs.getString("brand"))
                .model(rs.getString("model"))
                .year(rs.getInt("year"))
                .plate(rs.getString("plate"))
                .price(rs.getDouble("price"))
                .category(rs.getString("category"))
                .attributes(attributes)
                .build();
    }
}