package com.hotel.management.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Room {
    @Id
    private String id;
    private Integer no;
    private String type;
    private String status;
    private Integer capacity;
    private Double cost;

    @ToString.Exclude
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Feature.class)
    private List<Feature> stocks;

    @ToString.Exclude
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Facility.class)
    private List<Facility> facilities;
}
