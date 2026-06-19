package org.example.repositories;


import org.example.models.Rental;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental,String > {
    Optional<Rental> findByVehicleIdAndReturnDateTimeIsNull(String vehicleId);
}

