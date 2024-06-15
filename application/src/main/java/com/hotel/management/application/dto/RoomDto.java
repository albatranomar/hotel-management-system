package com.hotel.management.application.dto;

import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.entity.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class RoomDto extends RepresentationModel<RoomDto> {
    private String id;

    @PositiveOrZero
    private Integer no;

    @NotBlank(groups = OnCreate.class)
    private String type;

    private Room.Status status;

    @PositiveOrZero
    private Integer capacity;

    @PositiveOrZero
    private Double cost;


    private List<FeatureDto> features;
    private List<FacilityDto> facilities;
}
