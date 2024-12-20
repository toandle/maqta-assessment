package org.maqta.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.maqta.entity.CarPark;
import org.maqta.entity.CarParkInfo;
import org.maqta.model.*;
import org.maqta.repository.ICarParkRepository;
import org.maqta.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CarParkingService {

    @Autowired
    private ICarParkRepository carParkingRepository;

    @Value("${app.feature.radius}")
    private int radius;

    public List<FindParkingLotResponse> findNearestParkingLots(double latitude, double longitude, Integer page, Integer perPage) {
        PaginationUtil util = new PaginationUtil();
        Pagination pagination = util.standadizePagination(page, perPage);
        var pageable = PageRequest.of(pagination.getPage(), pagination.getPerPage(), Sort.by(Sort.Order.asc("distance")));

        Page<CarPark> carParks = carParkingRepository.findNearestCarParks(latitude, longitude, radius, pageable);

        return carParks.getContent().stream().map(this::transformToDTO).collect(Collectors.toList());
    }

    public void updateCarParkAvailability(List<CarParkAvailabilityMessage.CarParkData> carParkList) {
        Map<String, List<CarParkAvailabilityMessage.CarParkInfoDTO>> carParkMap = carParkList.stream()
                .collect(Collectors.toMap(
                        CarParkAvailabilityMessage.CarParkData::getCarParkNumber,
                        CarParkAvailabilityMessage.CarParkData::getCarParkInfo,
                        (existingValue, newValue) -> {
                            List<CarParkAvailabilityMessage.CarParkInfoDTO> combinedList = new ArrayList<>(existingValue);
                            combinedList.addAll(newValue);
                            return combinedList;
                        }
                ));
        List<String> carParkNumbers = new ArrayList<>(carParkMap.keySet());

        List<CarPark> carParks = carParkingRepository.findAllByCarParkNoIn(carParkNumbers);
        for (CarPark carPark: carParks) {
            List<CarParkAvailabilityMessage.CarParkInfoDTO> carParkInfoDTO = carParkMap.get(carPark.getCarParkNo());
            List<CarParkInfo> carParkInfo = carParkInfoDTO.stream().map(this::transFromCarParkInfoDTO2Entity).collect(Collectors.toList());

            var objectMapper = new ObjectMapper();
            String jsonNode = String.valueOf(objectMapper.valueToTree(carParkInfo));

            carPark.setCarParkInfo(jsonNode);
        }

        carParkingRepository.saveAll(carParks);
    }

    private CarParkInfo transFromCarParkInfoDTO2Entity(CarParkAvailabilityMessage.CarParkInfoDTO carParkInfoDTO) {
        var carParkInfo = new CarParkInfo();
        carParkInfo.setTotalLots(safeParse(carParkInfoDTO.getTotalLots()));
        carParkInfo.setLotType(carParkInfoDTO.getLotType());
        carParkInfo.setLotsAvailable(safeParse(carParkInfoDTO.getLotsAvailable()));

        return carParkInfo;
    }

    private static int safeParse(String numberString) {
        try {
            return Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private FindParkingLotResponse transformToDTO(CarPark carPark) {
        String carParkInfoString = carPark.getCarParkInfo();
        // Initialize ObjectMapper to deserialize the string into JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode carParkInfoNode = null;
        int totalLots = 0;
        int availableLots = 0;

        try {
            // Deserialize the string to JsonNode (if it's valid JSON)
            carParkInfoNode = objectMapper.readTree(carParkInfoString);
        } catch (Exception e) {
            System.out.println("Cannot parse car park info");
        }

        if (carParkInfoNode != null && carParkInfoNode.isArray()) {
            for (JsonNode item : carParkInfoNode) {
                if (item.has("total_lots")) {
                    totalLots += item.get("total_lots").asInt();
                }

                if (item.has("lots_available")) {
                    availableLots += item.get("lots_available").asInt();
                }
            }
        }

        var response = new FindParkingLotResponse();
        response.setAddress(carPark.getAddress());
        response.setLatitude(carPark.getLatitude());
        response.setLongitude(carPark.getLongitude());
        response.setTotalLots(totalLots);
        response.setAvailableLots(availableLots);

        return response;
    }
}
