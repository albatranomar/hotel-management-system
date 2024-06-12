package com.hotel.management.application.service;

import com.hotel.management.application.dto.BookingDto;
import com.hotel.management.application.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(String id);

    List<UserDto> getAllUsers();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(String id, UserDto userDto);

    void deleteUser(String id);

    List<BookingDto> getUserBookings(String id);
}
