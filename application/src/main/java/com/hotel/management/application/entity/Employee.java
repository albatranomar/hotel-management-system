package com.hotel.management.application.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = HouseKeeping.class)
    private List<HouseKeeping> tasks;
}
