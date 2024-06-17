package com.hotel.management.application.controller;

import com.hotel.management.application.dto.BookingDto;
import com.hotel.management.application.dto.RoomDto;
import com.hotel.management.application.dto.UserDto;
import com.hotel.management.application.entity.user.Role;
import com.hotel.management.application.entity.user.User;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.BookingService;
import com.hotel.management.application.service.RoomService;
import com.hotel.management.application.service.UserService;
import com.hotel.management.application.service.auth.AuthenticationService;
import com.hotel.management.application.service.impl.BookingServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/search")
@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
@RequiredArgsConstructor
public class SearchController {
    private final AuthenticationService authenticationService;
    private final BookingService bookingService;
    private final UserService userService;
    private final RoomService roomService;

    @GetMapping({"/", ""})
    public ResponseEntity<Object> search(HttpServletRequest request) {
        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found");

        RepresentationModel<?> model = new RepresentationModel<>();

        model.add(linkTo(methodOn(SearchController.class).reservations(null, null)).withRel("reservations"));
        if (user.getRole() == Role.ADMIN) model.add(linkTo(methodOn(SearchController.class).customers(null)).withRel("customers"));
        model.add(linkTo(methodOn(SearchController.class).rooms(null, null)).withRel("rooms"));

        return ResponseEntity.ok().body(model);
    }

    @GetMapping("/reservations")
    public ResponseEntity<Object> reservations(HttpServletRequest request, @RequestParam MultiValueMap<String, String> options) {
        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found");

        AtomicReference<List<BookingDto>> bookings = new AtomicReference<>(
                user.getRole() == Role.ADMIN ?
                        bookingService.getAllBookings() :
                        user.getBookings().stream().map(BookingServiceImpl::mapToDto).toList()
        );

        if (options != null) options.forEach((option, values) -> {
            System.out.println(option + " ::: " + Arrays.toString(values.toArray()));
            switch (option.toLowerCase()) {
                case "filter" -> values.forEach(value -> {
                    String[] key_value = value.split(":");
                    if (key_value.length == 2) {
                        String key = key_value[0].toLowerCase();
                        if (key.equals("customer_name")) {
                            bookings.set(
                                    bookings.get().stream().filter(bookingDto ->
                                                    (bookingDto.getCustomer().getFirstName() + bookingDto.getCustomer().getLastName()).toLowerCase()
                                                            .contains(key_value[1].toLowerCase()))
                                            .toList()
                            );
                        } else if (key.equals("status")) {
                            bookings.set(
                                    bookings.get().stream()
                                            .filter(bookingDto -> bookingDto.getStatus().name().toLowerCase().contains(key_value[1].toLowerCase())).toList()
                            );
                        }
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

        bookings.get().forEach(b -> BookingController.addLinkToDto(b, null));

        return ResponseEntity.ok().body(bookings.get());
    }

    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> customers(@RequestParam MultiValueMap<String, String> options) {
        AtomicReference<List<UserDto>> customers = new AtomicReference<>(userService.getAllCustomers());

        if (options != null) options.forEach((option, values) -> {
            System.out.println(option + " ::: " + Arrays.toString(values.toArray()));
            switch (option.toLowerCase()) {
                case "filter" -> values.forEach(value -> {
                    String[] key_value = value.split(":");
                    if (key_value.length == 2) {
                        String key = key_value[0].toLowerCase();
                        switch (key) {
                            case "name" -> customers.set(customers.get().stream().filter(customer ->
                                    (customer.getFirstName() + customer.getLastName())
                                            .toLowerCase()
                                            .contains(key_value[1].toLowerCase())).toList()
                            );
                            case "email" -> customers.set(customers.get().stream().filter(customer ->
                                    customer.getEmail().toLowerCase().contains(key_value[1].toLowerCase())).toList()
                            );
                            case "phone" -> customers.set(customers.get().stream().filter(customer ->
                                    customer.getPhoneNumber().contains(key_value[1])).toList()
                            );
                        }
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
                    customers.set(customers.get().subList(0, Math.min(limit, customers.get().size())));
                }
            }
        });

        customers.get().forEach(CustomerController::addLinksToCustomerDto);

        return ResponseEntity.ok().body(customers.get());
    }

    @GetMapping("/rooms")
    public ResponseEntity<Object> rooms(HttpServletRequest request, @RequestParam MultiValueMap<String, String> options) {
        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found");

        AtomicReference<List<RoomDto>> rooms = new AtomicReference<>(roomService.getAllRooms());

        if (options != null) options.forEach((option, values) -> {
            System.out.println(option + " ::: " + Arrays.toString(values.toArray()));
            switch (option.toLowerCase()) {
                case "filter" -> values.forEach(value -> {
                    String[] key_value = value.split(":");
                    if (key_value.length == 2) {
                        String key = key_value[0].toLowerCase();
                        switch (key) {
                            case "no" -> rooms.set(rooms.get().stream().filter(room ->
                                    room.getNo().toString().contains(key_value[1])).toList()
                            );
                            case "type" -> rooms.set(rooms.get().stream().filter(room ->
                                    room.getType().toLowerCase().contains(key_value[1].toLowerCase())).toList()
                            );
                            case "status" -> rooms.set(rooms.get().stream().filter(room ->
                                    room.getStatus().name().toLowerCase().contains(key_value[1].toLowerCase())).toList()
                            );
                            case "capacity" -> rooms.set(rooms.get().stream().filter(room ->
                                    room.getCapacity().toString().contains(key_value[1])).toList()
                            );
                            case "cost" -> rooms.set(rooms.get().stream().filter(room ->
                                    room.getCost().toString().contains(key_value[1])).toList()
                            );
                        }
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
                    rooms.set(rooms.get().subList(0, Math.min(limit, rooms.get().size())));
                }
            }
        });

        rooms.get().forEach(RoomController::addLinkToDto);

        return ResponseEntity.ok().body(rooms.get());
    }
}
