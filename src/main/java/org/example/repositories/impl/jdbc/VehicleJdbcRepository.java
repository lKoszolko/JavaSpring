package org.example.repositories.impl.jdbc;

import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jdbc")
public class VehicleJdbcRepository implements VehicleRepository {

    private final DataSource dataSource;

    public VehicleJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, category, brand, model, production_year, plate, price, attributes FROM vehicle";

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicles", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return vehicles;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT id, category, brand, model, production_year, plate, price, attributes FROM vehicle WHERE id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding vehicle by id", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
            insert(vehicle);
        } else {
            if (findById(vehicle.getId()).isPresent()) {
                update(vehicle);
            } else {
                insert(vehicle);
            }
        }
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting vehicle", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private void insert(Vehicle vehicle) {
        String sql = "INSERT INTO vehicle (id, category, brand, model, production_year, plate, price, attributes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setStatementParameters(stmt, vehicle);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while inserting vehicle", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private void update(Vehicle vehicle) {
        String sql = "UPDATE vehicle SET category = ?, brand = ?, model = ?, production_year = ?, plate = ?, price = ?, attributes = ? WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getCategory());
            stmt.setString(2, vehicle.getBrand());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setString(5, vehicle.getPlate());
            stmt.setDouble(6, vehicle.getPrice());
            stmt.setString(7, vehicle.getAttributes().toString());
            stmt.setString(8, vehicle.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while updating vehicle", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private void setStatementParameters(PreparedStatement stmt, Vehicle vehicle) throws SQLException {
        stmt.setString(1, vehicle.getId());
        stmt.setString(2, vehicle.getCategory());
        stmt.setString(3, vehicle.getBrand());
        stmt.setString(4, vehicle.getModel());
        stmt.setInt(5, vehicle.getYear());
        stmt.setString(6, vehicle.getPlate());
        stmt.setDouble(7, vehicle.getPrice());
        stmt.setString(8, vehicle.getAttributes().toString());
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(rs.getString("id"));
        vehicle.setCategory(rs.getString("category"));
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("production_year"));
        vehicle.setPlate(rs.getString("plate"));
        vehicle.setPrice(rs.getDouble("price"));
        return vehicle;
    }
}