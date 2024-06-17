package com.hotel.management.application.controller;

import com.hotel.management.application.dto.UserDto;
import com.hotel.management.application.dto.validation.OnUpdate;
import com.hotel.management.application.entity.user.User;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.UserService;
import com.hotel.management.application.service.auth.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @GetMapping({"/", ""})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllCustomers() {
        List<UserDto> customers = userService.getAllCustomers();
        if (customers.size() == 0) {
            throw new ResourceNotFoundException("There is currently no customers");
        } else {
            customers.forEach(CustomerController::addLinksToCustomerDto);

            return ResponseEntity.ok().body(customers);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getCustomerById(@PathVariable(name = "id") String id) {
        if (userService.getAllCustomers().stream().noneMatch(userDto -> userDto.getId().equals(id)))
            throw new ResourceNotFoundException("Customer with specified id(" + id + ") not found");

        UserDto userDto = userService.getUserById(id);

        addLinksToCustomerDto(userDto);

        return ResponseEntity.ok().body(userDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteCustomerById(@PathVariable(name = "id") String id) {
        if (userService.getAllCustomers().stream().noneMatch(userDto -> userDto.getId().equals(id)))
            throw new ResourceNotFoundException("Customer with specified id(" + id + ") not found");

        userService.deleteUser(id);

        // TODO: fix the response make it json with some links
        return ResponseEntity.ok().body("The customer was successfully deleted");
    }

    @GetMapping("/@me")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<UserDto> getMe(HttpServletRequest request) {
        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found");

        UserDto userDto = userService.getUserById(user.getId());

        addLinksToMeCustomer(userDto);

        return ResponseEntity.ok().body(userDto);
    }

    @PutMapping("/@me")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<UserDto> putMe(HttpServletRequest request, @RequestBody @Validated(OnUpdate.class) UserDto newData) {
        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found");

        UserDto userDto = userService.updateUser(user.getId(), newData);

        addLinksToMeCustomer(userDto);

        return ResponseEntity.ok().body(userDto);
    }

    public static void addLinksToCustomerDto(UserDto user) {
        user.add(linkTo(methodOn(CustomerController.class).getCustomerById(user.getId())).withSelfRel());
        user.add(linkTo(methodOn(CustomerController.class).deleteCustomerById(user.getId())).withRel("delete"));
    }

    public static void addLinksToMeCustomer(UserDto user) {
        user.add(linkTo(methodOn(CustomerController.class).getMe(null)).withSelfRel());
        user.add(linkTo(methodOn(CustomerController.class).putMe(null, null)).withRel("update"));
        user.add(linkTo(methodOn(AuthenticationController.class).changePassword(null,  null, null)).withRel("changePassword"));
        user.add(linkTo(methodOn(AuthenticationController.class).logout(null, null)).withRel("logout"));
        user.add(linkTo(methodOn(SearchController.class).reservations(null, null)).withRel("bookings"));
    }
}
