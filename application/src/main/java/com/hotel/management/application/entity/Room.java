package com.hotel.management.application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Room {
    public Room(String id, Integer no, String type, Status status, Integer capacity, Double cost, List<Feature> list, List<Facility> list1) {
        this(id, no, type, status, capacity, cost, list, list1, new ArrayList<>(), new ArrayList<>());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Integer no;
    private String type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;

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

    public static enum Status {
        AVAILABLE,
        RESERVED
    }
}
