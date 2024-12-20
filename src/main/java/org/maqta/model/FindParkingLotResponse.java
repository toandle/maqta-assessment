package org.maqta.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindParkingLotResponse {
    private String address;

    private double latitude;

    private double longitude;

    @JsonProperty("total_lots")
    private  int totalLots;

    @JsonProperty("available_lots")
    private int availableLots;
}
