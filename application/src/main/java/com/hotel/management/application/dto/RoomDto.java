package com.hotel.management.application.dto;

import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.entity.Booking;
import com.hotel.management.application.entity.HouseKeeping;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class RoomDto {
    private String id;

    @PositiveOrZero
    private Integer no;

    @NotBlank(groups = OnCreate.class)
    private String type;

    @NotBlank(groups = OnCreate.class)
    private String status;

    @PositiveOrZero
    private Integer capacity;

    @PositiveOrZero
    private Double cost;


    private List<FeatureDto> features;
    private List<FacilityDto> facilities;
}
