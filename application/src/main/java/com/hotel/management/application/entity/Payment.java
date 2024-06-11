package com.hotel.management.application.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment {
    @Id
    private String id;
    private String payment_status;

    @OneToOne(mappedBy = "payment")
    private Booking booking;
}
