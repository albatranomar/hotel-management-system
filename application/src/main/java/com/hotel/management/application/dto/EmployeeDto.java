package com.hotel.management.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotel.management.application.dto.validation.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class EmployeeDto extends RepresentationModel<EmployeeDto> {
    private String id;

    @NotBlank(groups = OnCreate.class)
    private String fname, lname, phoneNo, email;

    @NotNull(groups = OnCreate.class)
    @PositiveOrZero(message = "Salary should be equaled to or greater than zero.")
    private int salary;
}
