package com.hotel.management.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ErrorResponseDto extends RepresentationModel<ErrorResponseDto> {
    private int status;
    private String error;
    private String message;
}
