package org.maqta.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "car_park_info")
public class CarParkInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "total_lots", nullable = false)
    private int totalLots;

    @Column(name = "lot_type", nullable = false)
    private String lotType;

    @Column(name = "lots_available")
    private int lotsAvailable;

    @ManyToOne
    @JoinColumn(name = "car_park_id", nullable = false)
    private CarPark carPark;
}
