package com.hotel.management.application.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Violation {
    private final String fieldName;
    private final String message;
}
