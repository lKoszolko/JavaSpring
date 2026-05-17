package org.example.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rental")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rent_date", nullable = false)
    private String rentDateTime;

    @Column(name = "return_date")
    private String returnDateTime;

    @Override
    public String toString() {
        return "Wypożyczenie [ID: " + id +
                " | Pojazd: " + vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getPlate() + ")" +
                " | Użytkownik: " + user.getLogin() +
                " | Wypożyczono: " + rentDateTime +
                (returnDateTime != null ? " | Zwrócono: " + returnDateTime : " | [W TRAKCIE]") + "]";
    }
}