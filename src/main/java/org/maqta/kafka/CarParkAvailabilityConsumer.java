package org.maqta.kafka;

import org.maqta.model.CarParkAvailabilityMessage;
import org.maqta.service.CarParkingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CarParkAvailabilityConsumer {

    private final CarParkingService carParkingService;

    public CarParkAvailabilityConsumer(CarParkingService carParkingService) {
        this.carParkingService = carParkingService;
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.properties.topics.park-availability-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(CarParkAvailabilityMessage message) {
        this.carParkingService.updateCarParkAvailability(message.getItems().get(0).getCarParkData());
    }
}
