package org.maqta.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarParkAvailabilityMessage {
    private List<CarParkAvailabilityItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarParkAvailabilityItem {
        @JsonProperty("carpark_data")
        private List<CarParkData> carParkData;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarParkData {
        @JsonProperty("carpark_info")
        private List<CarParkInfoDTO> carParkInfo;

        @JsonProperty("carpark_number")
        private String carParkNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarParkInfoDTO {
        @JsonProperty("total_lots")
        private String totalLots;

        @JsonProperty("lot_type")
        private String lotType;

        @JsonProperty("lots_available")
        private String lotsAvailable;
    }
}
