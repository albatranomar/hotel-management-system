package com.hotel.management.application.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidationErrorResponse {
    private List<Violation> violations = new ArrayList<>();
}
