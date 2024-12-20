package org.maqta.controller;

import org.maqta.model.FindParkingLotResponse;
import org.maqta.service.CarParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("carparks/nearest")
public class CarParkingController {
    private final CarParkingService carParkingService;

    public CarParkingController(CarParkingService carParkingService) {
        this.carParkingService = carParkingService;
    }

    @GetMapping
    public ResponseEntity<List<FindParkingLotResponse>> findNearestParkingLots(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Integer page,
            @RequestParam(name = "per_page") Integer perPage) {
        if (latitude == null || longitude == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<FindParkingLotResponse> result = carParkingService.findNearestParkingLots(latitude, longitude, page, perPage);
        return ResponseEntity.ok(result);
    }
}
