package com.hotel.management.application.service;

import com.hotel.management.application.dto.BookingDto;
import com.hotel.management.application.dto.RoomDto;

import java.util.List;

public interface BookingService {
    List<BookingDto> getAllBookings();
    BookingDto getBookingById(String id);
    BookingDto updateBookingById(String id, BookingDto bookingDto);
    void deleteProviderById(String id);
    BookingDto createBooking(BookingDto bookingDto);
    boolean bookingExistsWithId(String id);
    List<RoomDto> getBookingRooms(String bookingId);
}
