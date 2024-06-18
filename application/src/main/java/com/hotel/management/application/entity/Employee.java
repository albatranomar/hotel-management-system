package com.hotel.management.application.entity;

import java.util.ArrayList;
import java.util.Date;
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
    public Employee(String id, String fname, String lname, String phoneNo, String email, Double salary) {
        this(id, fname, lname, phoneNo, email, salary, new ArrayList<>());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fname, lname, phoneNo, email;

    private Double salary;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.MERGE, fetch = FetchType.EAGER, targetEntity = HouseKeeping.class)
    private List<HouseKeeping> tasks;
}
