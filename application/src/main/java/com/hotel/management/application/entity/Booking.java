package com.hotel.management.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Booking {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToMany
    private List<Room> rooms;
}
