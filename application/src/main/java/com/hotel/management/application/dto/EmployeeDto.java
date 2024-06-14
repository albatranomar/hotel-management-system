package com.hotel.management.application.dto;

import com.hotel.management.application.dto.validation.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
public class EmployeeDto {
    private String id;

    @NotBlank(groups = OnCreate.class)
    private String fname, lname, phoneNo, email;

    @NotNull(groups = OnCreate.class)
    private Date dateOfBirth;

    @PositiveOrZero(message = "Salary should be equaled to or greater than zero.")
    private int salary;
}
