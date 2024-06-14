package com.hotel.management.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class FeatureDto extends RepresentationModel<FeatureDto> {
    private String id;

    @NotBlank
    private String fname;
}
