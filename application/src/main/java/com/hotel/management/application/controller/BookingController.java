package com.hotel.management.application.controller;

import com.hotel.management.application.dto.BookingDto;
import com.hotel.management.application.dto.RoomDto;
import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.dto.validation.OnUpdate;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping({"/", ""})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDto>> getAllBookings(@RequestParam MultiValueMap<String, String> options) {
        AtomicReference<List<BookingDto>> bookings = new AtomicReference<>(bookingService.getAllBookings());

        if (options != null) options.forEach((option, values) -> {
            switch (option.toLowerCase()) {
                case "filter" -> values.forEach(value -> {
                    String[] key_value = value.split(":");
                    if (key_value.length == 2) {
                        String key = key_value[0].toLowerCase();
                        if (key.equals("status"))
                            bookings.set(bookings.get().stream().filter(roomDto -> roomDto.getStatus().name().toLowerCase().contains(key_value[1].toLowerCase())).toList());
                    }
                });
                case "limit" -> {
                    int limit;
                    if (values.isEmpty())
                        limit = 100;
                    else {
                        try {
                            limit = Integer.parseInt(values.get(0));
                        } catch (Exception e) {
                            limit = 100;
                        }
                    }
                    bookings.set(bookings.get().subList(0, Math.min(limit, bookings.get().size())));
                }
            }
        });

        bookings.get().forEach(BookingController::addLinkToDto);
        return ResponseEntity.ok().body(bookings.get());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable String id) {
        BookingDto bookingDto = bookingService.getBookingById(id);
        addLinkToDto(bookingDto);
        return ResponseEntity.ok().body(bookingDto);
    }

    @PostMapping({"/", ""})
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<BookingDto> createBooking(@RequestBody @Validated(OnCreate.class) BookingDto bookingDto) {
        BookingDto createdBooking = bookingService.createBooking(bookingDto);
        addLinkToDto(createdBooking);
        return ResponseEntity.ok().body(createdBooking);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBooking(@PathVariable String id) {
        if (!bookingService.bookingExistsWithId(id))
            throw new ResourceNotFoundException("Booking with id " + id + " does not exist");

        bookingService.deleteBookingById(id);
        return ResponseEntity.ok().body("Booking deleted.");
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<String> cancelBooking(@PathVariable String id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok().body("Cancel request sent, pending confirmation.");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable String id,
                                                    @Validated(OnUpdate.class) @RequestBody BookingDto bookingDto) {
        BookingDto updatedBooking = bookingService.updateBookingById(id, bookingDto);
        addLinkToDto(updatedBooking);
        return ResponseEntity.ok().body(updatedBooking);
    }

    @GetMapping("/{id}/rooms")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<RoomDto>> getBookingRooms(@PathVariable String id) {
        return ResponseEntity.ok().body(bookingService.getBookingRooms(id));
    }

    @PostMapping("/{id}/checkin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> checkin(@PathVariable String id) {
        bookingService.checkin(id);
        return ResponseEntity.ok().body("Checked in successfully.");
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<String> checkout(@PathVariable String id) {
        bookingService.checkout(id);
        return ResponseEntity.ok().body("Checked out successfully.");
    }

    private static void addLinkToDto(BookingDto bookingDto) {
        bookingDto.add(linkTo(methodOn(BookingController.class).getBookingById(bookingDto.getId())).withSelfRel());
        bookingDto.add(linkTo(methodOn(BookingController.class).getBookingRooms(bookingDto.getId())).withRel("rooms"));
        bookingDto.add(linkTo(methodOn(BookingController.class).updateBooking(bookingDto.getId(), null)).withRel("update"));
        bookingDto.add(linkTo(methodOn(BookingController.class).cancelBooking(bookingDto.getId())).withRel("cancel"));
    }
}
