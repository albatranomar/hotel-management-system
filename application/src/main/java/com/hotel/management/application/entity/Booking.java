package com.hotel.management.application.entity;

import com.hotel.management.application.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    private Date checkInDate, checkOutDate;

    private Integer numAdults, numChildren;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.DEFAULT;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToMany
    private List<Room> rooms;

    public static enum Status {
        DEFAULT,
        CHECKED_IN,
        CHECKED_OUT,
        PENDING_CANCELLATION
    }
}
