package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.BookingDto;
import com.hotel.management.application.dto.PaymentDto;
import com.hotel.management.application.dto.RoomDto;
import com.hotel.management.application.entity.Booking;
import com.hotel.management.application.entity.Payment;
import com.hotel.management.application.entity.Room;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.BookingRepository;
import com.hotel.management.application.repository.PaymentRepository;
import com.hotel.management.application.repository.RoomRepository;
import com.hotel.management.application.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, PaymentRepository paymentRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.roomRepository  = roomRepository;
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
            booking.setStatus(bookingDto.getStatus());
            booking.setCheckInDate(bookingDto.getCheckInDate());
            booking.setCheckOutDate(bookingDto.getCheckOutDate());
            booking.setNumAdults(bookingDto.getNumAdults());
            booking.setNumChildren(bookingDto.getNumChildren());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid value provided.");
        }

        return mapToDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBookingById(String id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        booking.getRooms().forEach(room -> {
            room.setStatus(Room.Status.AVAILABLE);
            roomRepository.save(room);
        });

        bookingRepository.deleteById(id);
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        Double cost = 0.0;
        if (bookingDto.getRooms() != null) {
            for (RoomDto roomDto : bookingDto.getRooms()) {
                cost += roomDto.getCost();
                roomDto.setStatus(Room.Status.RESERVED);
                roomRepository.save(RoomServiceImpl.mapToEntity(roomDto));
            }
        }

        Booking booking = mapToEntity(bookingDto);

        Payment payment = new Payment();
        payment.setBill(cost);

        booking.setPayment(paymentRepository.save(payment));
        return mapToDto(bookingRepository.save(booking));
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
    public PaymentDto getPaymentFor(String id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        Payment payment = booking.getPayment();
        return new PaymentDto(payment.getId(), payment.getPayment_status(), payment.getBill(),
                BookingServiceImpl.mapToDto(payment.getBooking()));
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

        if (!booking.getStatus().equals(Booking.Status.CHECKED_IN))
            throw new BadRequestException("Must be checked in to check out.");

        booking.getRooms().forEach(room -> {
            room.setStatus(Room.Status.AVAILABLE);
            roomRepository.save(room);
        });

        booking.setStatus(Booking.Status.CHECKED_OUT);
        booking.getPayment().setPayment_status(Payment.Status.PAID);
        bookingRepository.save(booking);
    }

    @Override
    public void checkin(String id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (!booking.getStatus().equals(Booking.Status.DEFAULT))
            throw new BadRequestException("Can't check in to this booking.");

        booking.setStatus(Booking.Status.CHECKED_IN);
        bookingRepository.save(booking);
    }

    @Override
    public void addRoom(String bookingId, String roomId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        if (booking.getRooms().contains(room))
            throw new BadRequestException("Booking already has this room.");

        room.setStatus(Room.Status.RESERVED);

        booking.getRooms().add(room);
        booking.getPayment().setBill(booking.getPayment().getBill() + room.getCost());
        roomRepository.save(room);
        bookingRepository.save(booking);
    }

    @Override
    public void deleteRoom(String bookingId, String roomId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        if (!booking.getRooms().contains(room))
            throw new BadRequestException("Booking does not have this room.");

        room.setStatus(Room.Status.AVAILABLE);

        booking.getRooms().remove(room);
        booking.getPayment().setBill(booking.getPayment().getBill() - room.getCost());
        roomRepository.save(room);
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
