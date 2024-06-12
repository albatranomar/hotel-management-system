package com.hotel.management.application.entity;

import java.sql.Date;
import java.util.ArrayList;
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
    public Employee(String id, String fname, String lname, String phoneNo, String email, Date dateOfBirth, int salary) {
        this(id, fname, lname, phoneNo, email, dateOfBirth, salary, new ArrayList<>());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fname, lname, phoneNo, email;

    private Date dateOfBirth;

    private int salary;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = HouseKeeping.class)
    private List<HouseKeeping> tasks;
}
