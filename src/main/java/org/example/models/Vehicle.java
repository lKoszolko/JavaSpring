package org.example.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(columnDefinition = "NUMERIC")
    private double price;
    private String category;
    private String brand;
    private String model;

    private int year;
    private String plate;

    @Column(name="is_rented", nullable = false)
    private boolean rented;

    @Column(columnDefinition = "jsonb")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> attributes = new HashMap<>();

    @Embedded
    private Coordinates currentLocation;

    @Builder
    public Vehicle(String id,
                   String category,
                   String brand,
                   String model,
                   int year,
                   String plate,
                   double price,
                   Map<String, Object> attributes) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.rented = false;
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }

    public Map<String, Object> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        return this.attributes;
    }

    public Object getAttribute(String key) {
        if (this.attributes == null) return null;
        return this.attributes.get(key);
    }

    public void addAttribute(String key, Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }

    public void removeAttribute(String key) {
        if (this.attributes != null) {
            this.attributes.remove(key);
        }
    }

    public Vehicle copy() {
        return Vehicle.builder()
                .id(this.id)
                .category(this.category)
                .brand(this.brand)
                .model(this.model)
                .year(this.year)
                .plate(this.plate)
                .price(this.price)
                .attributes(this.attributes == null ? new HashMap<>() : new HashMap<>(this.attributes))
                .build();
    }
}