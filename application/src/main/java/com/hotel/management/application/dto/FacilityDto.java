package com.hotel.management.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class FacilityDto extends RepresentationModel<FacilityDto> {
    private String id;

    @NotBlank
    private String fname;
}
