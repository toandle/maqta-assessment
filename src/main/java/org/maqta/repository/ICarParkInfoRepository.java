package org.maqta.repository;

import org.maqta.entity.CarParkInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ICarParkInfoRepository extends JpaRepository<CarParkInfo, UUID> {
}
