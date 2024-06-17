package com.hotel.management.application.service;

import com.hotel.management.application.dto.BookingDto;
import com.hotel.management.application.dto.PaymentDto;
import com.hotel.management.application.dto.RoomDto;

import java.util.List;

public interface BookingService {
    List<BookingDto> getAllBookings();
    BookingDto getBookingById(String id);
    BookingDto updateBookingById(String id, BookingDto bookingDto);
    void deleteBookingById(String id);
    BookingDto createBooking(BookingDto bookingDto);
    boolean bookingExistsWithId(String id);
    List<RoomDto> getBookingRooms(String bookingId);
    PaymentDto getPaymentFor(String id);
    void cancelBooking(String bookingId);
    void checkout(String id);
    void checkin(String id);
    void addRoom(String bookingId, String roomId);
    void deleteRoom(String bookingId, String roomId);
}
