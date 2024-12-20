package org.maqta.service;

import org.maqta.entity.CarPark;
import org.maqta.entity.CarParkInfo;
import org.maqta.model.*;
import org.maqta.repository.ICarParkInfoRepository;
import org.maqta.repository.ICarParkRepository;
import org.maqta.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarParkingService {

    @Autowired
    private ICarParkRepository carParkingRepository;

    @Autowired
    private ICarParkInfoRepository carParkInfoRepository;

    @Value("${app.feature.radius}")
    private int radius;

    public List<FindParkingLotResponse> findNearestParkingLots(double latitude, double longitude, Integer page, Integer perPage) {
        PaginationUtil util = new PaginationUtil();
        Pagination pagination = util.standadizePagination(page, perPage);
        var pageable = PageRequest.of(pagination.getPage(), pagination.getPerPage(), Sort.by(Sort.Order.asc("distance")));

        Page<CarPark> carParks = carParkingRepository.findNearestCarParks(latitude, longitude, radius, pageable);

        return carParks.getContent().stream().map(this::transformToDTO).collect(Collectors.toList());
    }

    @Transactional
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
        List<CarParkInfo> updatedCarParkInfos = new ArrayList<>();
        List<CarParkInfo> newCarParkInfos = new ArrayList<>();

        for (CarPark carPark: carParks) {
            List<CarParkAvailabilityMessage.CarParkInfoDTO> carParkInfoDTOList = carParkMap.get(carPark.getCarParkNo());
            for (CarParkAvailabilityMessage.CarParkInfoDTO carParkInfoDTO : carParkInfoDTOList) {
                // Check if there's an existing entry for the same lotType
                var existingCarParkInfo = carPark.getCarParkInfo()
                        .stream()
                        .filter(info -> Objects.equals(info.getLotType(), carParkInfoDTO.getLotType()))
                        .findFirst();

                if (existingCarParkInfo.isPresent()) {
                    // Update existing entry
                    CarParkInfo carParkInfo = existingCarParkInfo.get();
                    carParkInfo.setTotalLots(safeParse(carParkInfoDTO.getTotalLots()));
                    carParkInfo.setLotsAvailable(safeParse(carParkInfoDTO.getLotsAvailable()));
                    updatedCarParkInfos.add(carParkInfo);
                } else {
                    // Create new entry
                    CarParkInfo carParkInfo = new CarParkInfo();
                    carParkInfo.setTotalLots(safeParse(carParkInfoDTO.getTotalLots()));
                    carParkInfo.setLotType(carParkInfoDTO.getLotType());
                    carParkInfo.setLotsAvailable(safeParse(carParkInfoDTO.getLotsAvailable()));
                    carParkInfo.setCarPark(carPark);
                    newCarParkInfos.add(carParkInfo);
                }
            }
        }

        // Save updated entries
        if (!updatedCarParkInfos.isEmpty()) {
            carParkInfoRepository.saveAll(updatedCarParkInfos);
        }


        // Save new entries
        if(!newCarParkInfos.isEmpty()) {
            carParkInfoRepository.saveAll(newCarParkInfos);
        }
    }

    private CarParkInfo transFromCarParkInfoDTO2Entity(CarParkAvailabilityMessage.CarParkInfoDTO carParkInfoDTO, CarPark carPark) {
        var existingCarParkInfo = carPark.getCarParkInfo()
                .stream()
                .filter((CarParkInfo info) -> Objects.equals(info.getLotType(), carParkInfoDTO.getLotType()))
                .findFirst();
        var carParkInfo = new CarParkInfo();
        carParkInfo.setTotalLots(safeParse(carParkInfoDTO.getTotalLots()));
        carParkInfo.setLotType(carParkInfoDTO.getLotType());
        carParkInfo.setLotsAvailable(safeParse(carParkInfoDTO.getLotsAvailable()));
        carParkInfo.setCarPark(carPark);

        existingCarParkInfo.ifPresent(parkInfo -> carParkInfo.setId(parkInfo.getId()));

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
        int totalLots = 0;
        int availableLots = 0;
        List<CarParkInfo> carParkInfoList = carPark.getCarParkInfo();

        if (carParkInfoList != null) {
            for (CarParkInfo carParkInfo : carParkInfoList) {
                totalLots += carParkInfo.getTotalLots();
                availableLots += carParkInfo.getLotsAvailable();
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
