package org.maqta.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "car_park")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarPark {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "car_park_no", unique = true, nullable = false)
    private String carParkNo;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, name = "x_coord")
    private double longitude;
    @Column(nullable = false, name = "y_coord")
    private double latitude;

    @Column(name = "car_park_type", nullable = false)
    private String carParkType;

    @Column(name = "type_of_parking_system", nullable = false)
    private String typeOfParkingSystem;

    @Column(name = "short_term_parking", nullable = false)
    private String shortTermParking;

    @Column(name = "free_parking", nullable = false)
    private String freeParking;

    @Column(name = "night_parking", nullable = false)
    private String nightParking;

    @Column(name = "car_park_desks", nullable = false)
    private int carParkDesks;

    @Column(name = "gantry_height", nullable = false)
    private double gantryHeight;

    @Column(name = "car_park_basement", nullable = false)
    private String carParkBasement;

    @OneToMany(mappedBy = "carPark", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CarParkInfo> carParkInfo = new ArrayList<>();
}
