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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Integer no;
    private String type;
    private String status;
    private Integer capacity;
    private Double cost;

    @ToString.Exclude
    @ManyToMany
    private List<Feature> features;

    @ToString.Exclude
    @ManyToMany
    private List<Facility> facilities;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = HouseKeeping.class)
    private List<HouseKeeping> tasks;

    @ManyToMany
    @JoinTable(
            name = "room_booking",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "booking_id"))
    private List<Booking> bookings;
}
