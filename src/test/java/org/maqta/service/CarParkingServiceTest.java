package org.maqta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.maqta.model.CarParkAvailabilityMessage;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.maqta.entity.CarPark;
import org.maqta.model.FindParkingLotResponse;
import org.maqta.repository.ICarParkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CarParkingServiceTest {

    @InjectMocks
    private CarParkingService carParkingService;

    @Mock
    private ICarParkRepository carParkRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindNearestParkingLots() {
        // Prepare input
        double latitude = 10.0;
        double longitude = 20.0;
        int page = 0;
        int perPAge =10;

        // Prepare mock data
        CarPark carPark1 = new CarPark();
        carPark1.setAddress("Address 1");
        carPark1.setLatitude(10.1);
        carPark1.setLongitude(20.1);
        carPark1.setCarParkNo("CP1");

        CarPark carPark2 = new CarPark();
        carPark2.setAddress("Address 2");
        carPark2.setLatitude(10.2);
        carPark2.setLongitude(20.2);
        carPark2.setCarParkNo("CP2");

        List<CarPark> carParkList = Arrays.asList(carPark1, carPark2);

        // Prepare a Page object to be returned by the repository mock
        Page<CarPark> carParkPage = new PageImpl<>(carParkList, PageRequest.of(0, 10), carParkList.size());

        // Mock the repository method
        when(carParkRepository.findNearestCarParks(any(Double.class), any(Double.class), any(Double.class), any())).thenReturn(carParkPage);

        // Call the service method
        List<FindParkingLotResponse> response = carParkingService.findNearestParkingLots(latitude, longitude, page, perPAge);

        // Verify the result
        assertEquals(2, response.size());
        assertEquals("Address 1", response.get(0).getAddress());
        assertEquals("Address 2", response.get(1).getAddress());
    }

    @Test
    public void testUpdateCarParkAvailability() {
        // Step 1: Prepare input data (car park availability data)
        CarParkAvailabilityMessage.CarParkInfoDTO carParkInfoDTO1 = new CarParkAvailabilityMessage.CarParkInfoDTO();
        carParkInfoDTO1.setTotalLots("105");
        carParkInfoDTO1.setLotType("C");
        carParkInfoDTO1.setLotsAvailable("86");

        CarParkAvailabilityMessage.CarParkData carParkData1 = new CarParkAvailabilityMessage.CarParkData();
        carParkData1.setCarParkNumber("CP1");
        carParkData1.setCarParkInfo(Arrays.asList(carParkInfoDTO1));

        List<CarParkAvailabilityMessage.CarParkData> carParkList = Arrays.asList(carParkData1);

        // Step 2: Prepare a CarPark entity that will be returned by the mock repository
        CarPark carPark = new CarPark();
        carPark.setCarParkNo("CP1");
        carPark.setAddress("Address 1");
        carPark.setLatitude(10.1);
        carPark.setLongitude(20.1);

        // Mocking the CarPark entity to return for the provided car park number
        when(carParkRepository.findAllByCarParkNoIn(any())).thenReturn(Arrays.asList(carPark));

        // Step 3: Call the method under test
        carParkingService.updateCarParkAvailability(carParkList);

        // Step 4: Verify interactions with the mock repository
        // Verify that `findAllByCarParkNoIn` was called with the correct car park number
        verify(carParkRepository).findAllByCarParkNoIn(Arrays.asList("CP1"));

        // Step 5: Verify the transformation logic (mapping DTO to entity)
        // Check if the `carParkInfo` field of the CarPark entity is updated correctly
        assertEquals(1, carPark.getCarParkInfo().size()); // Ensure there is 1 CarParkInfo object
        assertEquals(105, carPark.getCarParkInfo().get(0).getTotalLots()); // Ensure totalLots is mapped correctly
        assertEquals(86, carPark.getCarParkInfo().get(0).getLotsAvailable()); // Ensure lotsAvailable is mapped correctly

        // Step 6: Verify that the repository's `saveAll` method is called to persist the updates
        verify(carParkRepository).saveAll(Arrays.asList(carPark));
    }

}
