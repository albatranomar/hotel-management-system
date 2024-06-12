package com.hotel.management.application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @NotNull
    public User(String id, Roles role, String firstName, String lastName, String email, String password) {
        this(id, role, firstName, lastName, email, password, new ArrayList<>());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Roles role;

    private String firstName, lastName;

    private String email, password;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Booking.class)
    private List<Booking> bookings;

    public static enum Roles {
        ADMIN,
        CUSTOMER
    }
}
