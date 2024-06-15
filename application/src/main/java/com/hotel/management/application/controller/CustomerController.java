package com.hotel.management.application.controller;

import com.hotel.management.application.dto.UserDto;
import com.hotel.management.application.entity.user.User;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.UserService;
import com.hotel.management.application.service.auth.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
//            customers.stream().map(customer -> customer.add(linkTo(methodOn(CustomerController.class).)))

            return ResponseEntity.ok().body(customers);
        }
    }

    @GetMapping("/@me")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<UserDto> getMe(HttpServletRequest request) throws IOException {
        User user = authenticationService.getUser(request);
        if (user == null) throw new ResourceNotFoundException("User not found");

        UserDto userDto = userService.getUserById(user.getId());
        Link logoutLink = Link.of("/api/v1/auth/logout", "logout");

        userDto.add(linkTo(methodOn(CustomerController.class).getMe(null)).withSelfRel());
        userDto.add(linkTo(methodOn(AuthenticationController.class).changePassword(null,  null, null)).withRel("changePassword"));
        userDto.add(logoutLink);

        return ResponseEntity.ok().body(userDto);
    }
}
