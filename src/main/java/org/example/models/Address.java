package org.example.models;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Address {
    private String city, street, zipCode, buildingNumber;
}
