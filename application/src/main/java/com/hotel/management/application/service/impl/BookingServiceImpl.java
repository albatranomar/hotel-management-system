package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.BookingDto;
import com.hotel.management.application.dto.RoomDto;
import com.hotel.management.application.entity.Booking;
import com.hotel.management.application.entity.Payment;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.BookingRepository;
import com.hotel.management.application.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<BookingDto> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream().map(BookingServiceImpl::mapToDto).toList();
    }

    @Override
    public BookingDto getBookingById(String id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking",
                "id", id));
        return BookingServiceImpl.mapToDto(booking);
    }

    @Override
    public BookingDto updateBookingById(String id, BookingDto bookingDto) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking"
                , "id", id));

        try {
            booking.setCustomer(UserServiceImpl.mapToEntity(bookingDto.getCustomer()));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid value provided.");
        }

        return mapToDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBookingById(String id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        return mapToDto(bookingRepository.save(mapToEntity(bookingDto)));
    }

    @Override
    public boolean bookingExistsWithId(String id) {
        return bookingRepository.existsById(id);
    }

    @Override
    public List<RoomDto> getBookingRooms(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException(
                "Booking", "id", bookingId));
        return booking.getRooms().stream().map(RoomServiceImpl::mapToDto).toList();
    }

    @Override
    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
        booking.setStatus(Booking.Status.PENDING_CANCELLATION);
        bookingRepository.save(booking);
    }

    @Override
    public void checkout(String id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        booking.setStatus(Booking.Status.CHECKED_OUT);
        booking.getPayment().setPayment_status(Payment.Status.PAID);
        bookingRepository.save(booking);
    }

    @Override
    public void checkin(String id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        booking.setStatus(Booking.Status.CHECKED_IN);
        bookingRepository.save(booking);
    }

    public static BookingDto mapToDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getCheckInDate(), booking.getCheckOutDate(),
                booking.getNumAdults(), booking.getNumChildren(), booking.getStatus(),
                UserServiceImpl.mapToDto(booking.getCustomer()),
                booking.getRooms().stream().map(RoomServiceImpl::mapToDto).toList());
    }

    public static Booking mapToEntity(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate(),
                bookingDto.getNumAdults(), bookingDto.getNumChildren(), bookingDto.getStatus(),
                UserServiceImpl.mapToEntity(bookingDto.getCustomer()), null,
                bookingDto.getRooms().stream().map(RoomServiceImpl::mapToEntity).toList());
    }
}
