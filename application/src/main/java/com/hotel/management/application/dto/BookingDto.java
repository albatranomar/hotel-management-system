package com.hotel.management.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookingDto {
    private String id;

    private UserDto customer;

    private List<RoomDto> rooms;
}
