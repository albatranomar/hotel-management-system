package com.hotel.management.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Facility {
    public Facility(String id, String fname) {
        this(id, fname, new ArrayList<>());
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String fname;

    @ManyToMany
    @JoinTable(
            name = "room_facility",
            joinColumns = @JoinColumn(name = "facility_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id"))
    private List<Room> rooms;
}
