package com.hotel.management.application.dto;

import com.hotel.management.application.entity.Booking;
import com.hotel.management.application.entity.HouseKeeping;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class RoomDto {
    private String id;
    private Integer no;
    private String type;
    private String status;
    private Integer capacity;
    private Double cost;
    private List<FeatureDto> features;
    private List<FacilityDto> facilities;
}
