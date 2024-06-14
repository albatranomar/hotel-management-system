package com.hotel.management.application.dto;

import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.entity.Booking;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class BookingDto {
    private String id;

    @NotNull(groups = OnCreate.class)
    private Date checkInDate, checkOutDate;

    @NotNull(groups = OnCreate.class)
    @PositiveOrZero
    private Integer numAdults, numChildren;

    private Booking.Status status;

    private UserDto customer;

    private List<RoomDto> rooms;
}
