package com.hotel.management.application.entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Employee {
    @Id
    private String id;

    private String fname, lname, phoneNo, email;

    private Date dateOfBirth;

    private int salary;
}
