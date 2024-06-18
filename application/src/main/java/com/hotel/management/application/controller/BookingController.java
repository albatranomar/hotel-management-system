package com.hotel.management.application.controller;

import com.hotel.management.application.dto.BookingDto;
import com.hotel.management.application.dto.PaymentDto;
import com.hotel.management.application.dto.RoomDto;
import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.dto.validation.OnUpdate;
import com.hotel.management.application.entity.Booking;
import com.hotel.management.application.entity.Room;
import com.hotel.management.application.entity.user.Role;
import com.hotel.management.application.entity.user.User;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.BookingService;
import com.hotel.management.application.service.RoomService;
import com.hotel.management.application.service.UserService;
import com.hotel.management.application.service.auth.AuthenticationService;
import com.hotel.management.application.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "Exposes APIs to manage bookings and all their details.")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final RoomService roomService;
    private final AuthenticationService authenticationService;

    @Operation(description = "REST API to retrieve all bookings.", summary = "Retrieve all bookings")
    @GetMapping({"/", ""})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDto>> getAllBookings(HttpServletRequest request,
                                                           @RequestParam MultiValueMap<String, String> options) {
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

        bookings.get().forEach(booking -> addLinkToDto(booking, request));
        return ResponseEntity.ok().body(bookings.get());
    }

    @Operation(description = "REST API to retrieve booking by its ID.", summary = "Retrieve booking by its ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<BookingDto> getBookingById(HttpServletRequest request, @PathVariable String id) {
        BookingDto bookingDto = bookingService.getBookingById(id);
        addLinkToDto(bookingDto, request);
        return ResponseEntity.ok().body(bookingDto);
    }

    @Operation(description = "REST API to add a new booking to the system.", summary = "Add a new booking")
    @PostMapping({"/", ""})
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<BookingDto> createBooking(HttpServletRequest request,
                                                    @RequestBody @Validated(OnCreate.class) BookingDto bookingDto) {
        if (bookingDto.getStatus() == null)
            bookingDto.setStatus(Booking.Status.DEFAULT);
        if (bookingDto.getRooms() == null)
            bookingDto.setRooms(new ArrayList<>());

        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found.");

        if (user.getRole().equals(Role.CUSTOMER))
            bookingDto.setCustomer(UserServiceImpl.mapToDto(user));
        else if (bookingDto.getCustomer() == null || bookingDto.getCustomer().getId() == null)
            throw new BadRequestException("Admin users must specify a customer.");

        if (bookingDto.getRooms() != null) {
            for (int i = 0; i < bookingDto.getRooms().size(); i++) {
                RoomDto roomDto = bookingDto.getRooms().get(i);
                if (roomDto.getId() == null)
                    throw new BadRequestException("Provide room id for each room.");

                RoomDto room = roomService.getRoomById(roomDto.getId());
                if (room.getStatus().equals(Room.Status.RESERVED))
                    throw new BadRequestException("Room with id " + roomDto.getId() + " is reserved");

                bookingDto.getRooms().set(i, room);
            }
        }

        bookingDto.setCustomer(userService.getUserById(bookingDto.getCustomer().getId()));
        BookingDto createdBooking = bookingService.createBooking(bookingDto);
        addLinkToDto(createdBooking, request);
        return ResponseEntity.ok().body(createdBooking);
    }

    @Operation(description = "REST API to delete a booking from the system by its ID.", summary = "Delete a booking")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBooking(@PathVariable String id) {
        if (!bookingService.bookingExistsWithId(id))
            throw new ResourceNotFoundException("Booking with id " + id + " does not exist");

        bookingService.deleteBookingById(id);
        return ResponseEntity.ok().body("Booking canceled.");
    }

    @Operation(description = "REST API to cancel a booking.", summary = "cancel a booking")
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<String> cancelBooking(HttpServletRequest request, @PathVariable String id) {
        BookingDto bookingDto = bookingService.getBookingById(id);
        if (bookingDto.getStatus().equals(Booking.Status.CHECKED_IN))
            throw new BadRequestException("Can't cancel a checked in booking.");

        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found.");

        if (user.getRole().equals(Role.ADMIN))
            return deleteBooking(id);
        else if (!userService.hasBooking(user.getId(), id))
            throw new AccessDeniedException("Customer can't cancel a reservation of another customer.");

        bookingService.cancelBooking(id);
        return ResponseEntity.ok().body("Cancel request sent, pending confirmation.");
    }

    @Operation(description = "REST API to update a booking by its ID.", summary = "update a booking")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<BookingDto> updateBooking(HttpServletRequest request, @PathVariable String id,
                                                    @Validated(OnUpdate.class) @RequestBody BookingDto bookingDto) {
        BookingDto oldBookingDto = bookingService.getBookingById(id);
        if (oldBookingDto.getStatus().equals(Booking.Status.CHECKED_IN))
            throw new BadRequestException("Can't update a checked in booking.");

        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found.");

        if (user.getRole().equals(Role.CUSTOMER) && !userService.hasBooking(user.getId(), id))
            throw new AccessDeniedException("Customer can't update a reservation of another customer.");

        BookingDto updatedBooking = bookingService.updateBookingById(id, bookingDto);
        addLinkToDto(updatedBooking, request);
        return ResponseEntity.ok().body(updatedBooking);
    }

    @Operation(description = "REST API to retrieve the rooms of a booking by its ID.", summary = "Retrieve the rooms of a booking")
    @GetMapping("/{id}/rooms")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<RoomDto>> getBookingRooms(HttpServletRequest request, @PathVariable String id) {
        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found.");

        if (user.getRole().equals(Role.CUSTOMER) && !userService.hasBooking(user.getId(), id))
            throw new AccessDeniedException("Customer can't see reservations of another customer.");

        return ResponseEntity.ok().body(bookingService.getBookingRooms(id));
    }

    @Operation(description = "REST API to add a room to a booking by both IDs.", summary = "Add a room to a booking")
    @PostMapping("/{bookingId}/rooms/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<String> addBookingRoom(HttpServletRequest request, @PathVariable String bookingId, @PathVariable String roomId) {
        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found.");

        if (user.getRole().equals(Role.CUSTOMER) && !userService.hasBooking(user.getId(), bookingId))
            throw new AccessDeniedException("Customer can't update a reservations of another customer.");

        bookingService.addRoom(bookingId, roomId);
        return ResponseEntity.ok().body("Room added to booking.");
    }

    @Operation(description = "REST API to remove a room from a booking by both IDs.", summary = "Remove a room from a booking")
    @DeleteMapping("/{bookingId}/rooms/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<String> removeBookingRoom(HttpServletRequest request, @PathVariable String bookingId, @PathVariable String roomId) {
        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found.");

        if (user.getRole().equals(Role.CUSTOMER) && !userService.hasBooking(user.getId(), bookingId))
            throw new AccessDeniedException("Customer can't update a reservations of another customer.");

        bookingService.deleteRoom(bookingId, roomId);
        return ResponseEntity.ok().body("Room removed from booking.");
    }

    @Operation(description = "REST API to check into a booking by its ID.", summary = "Check into a booking")
    @PostMapping("/{id}/checkin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> checkin(@PathVariable String id) {
        bookingService.checkin(id);
        return ResponseEntity.ok().body("Checked in successfully.");
    }

    @Operation(description = "REST API to check out of a booking by its ID.", summary = "Check out of a booking")
    @PostMapping("/{id}/checkout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> checkout(@PathVariable String id) {
        bookingService.checkout(id);
        return ResponseEntity.ok().body("Checked out successfully.");
    }

    @Operation(description = "REST API to retrieve the payment of a booking by its ID.", summary = "Retrieve the payment of a booking")
    @GetMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<PaymentDto> getPayment(HttpServletRequest request, @PathVariable String id) {
        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found.");

        if (user.getRole().equals(Role.CUSTOMER) && !userService.hasBooking(user.getId(), id))
            throw new AccessDeniedException("Customer can't see reservations of another customer.");

        return ResponseEntity.ok().body(bookingService.getPaymentFor(id));
    }

    public static void addLinkToDto(BookingDto bookingDto, HttpServletRequest request) {
        bookingDto.add(linkTo(methodOn(BookingController.class).getBookingById(request, bookingDto.getId())).withSelfRel());
        bookingDto.add(linkTo(methodOn(BookingController.class).getBookingRooms(request, bookingDto.getId())).withRel("rooms"));
        bookingDto.add(linkTo(methodOn(BookingController.class).updateBooking(request, bookingDto.getId(), null)).withRel("update"));
        bookingDto.add(linkTo(methodOn(BookingController.class).cancelBooking(request, bookingDto.getId())).withRel("cancel"));
        bookingDto.add(linkTo(methodOn(BookingController.class).addBookingRoom(request, bookingDto.getId(), null)).withRel("add-room"));
        bookingDto.add(linkTo(methodOn(BookingController.class).removeBookingRoom(request, bookingDto.getId(), null)).withRel("remove-room"));
        bookingDto.add(linkTo(methodOn(BookingController.class).getPayment(request, bookingDto.getId())).withRel("payment"));
        CustomerController.addLinksToCustomerDto(bookingDto.getCustomer());
        bookingDto.getRooms().forEach(RoomController::addLinkToDto);
    }
}
