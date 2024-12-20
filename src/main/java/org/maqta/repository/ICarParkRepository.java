package org.maqta.repository;

import org.maqta.entity.CarPark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ICarParkRepository extends JpaRepository<CarPark, UUID> {

    @Query(value = "SELECT *, (" +
            "   6371 * acos(" +
            "     cos(radians(:latitude)) * cos(radians(y_coord)) * " +
            "     cos(radians(x_coord) - radians(:longitude)) + " +
            "     sin(radians(:latitude)) * sin(radians(y_coord)) " +
            "   )" +
            " ) AS distance " +
            "FROM car_park " +
            "WHERE (" +
            "   6371 * acos(" +
            "     cos(radians(:latitude)) * cos(radians(y_coord)) * " +
            "     cos(radians(x_coord) - radians(:longitude)) + " +
            "     sin(radians(:latitude)) * sin(radians(y_coord)) " +
            "   )" +
            " ) <= :radius " +
            "ORDER BY distance ASC",
            nativeQuery = true)
    Page<CarPark> findNearestCarParks(@Param("latitude") double latitude,
                                      @Param("longitude") double longitude,
                                      @Param("radius") double radius,
                                      Pageable pageable);

    List<CarPark> findAllByCarParkNoIn(List<String> carParkNos);
}
