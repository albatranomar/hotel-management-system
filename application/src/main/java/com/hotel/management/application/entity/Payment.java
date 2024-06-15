package com.hotel.management.application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status payment_status = Status.PENDING;

    @OneToOne(mappedBy = "payment")
    private Booking booking;

    public static enum Status {
        PAID,
        PENDING
    }
}
