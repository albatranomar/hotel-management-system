package com.hotel.management.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
public class EmployeeDto {
    private String id;

    private String fname, lname, phoneNo, email;

    private Date dateOfBirth;

    @PositiveOrZero(message = "Salary should be equaled to or greater than zero.")
    private int salary;
}
