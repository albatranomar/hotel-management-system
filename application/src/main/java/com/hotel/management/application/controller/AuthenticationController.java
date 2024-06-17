package com.hotel.management.application.controller;

import com.hotel.management.application.dto.auth.AuthenticationRequestDto;
import com.hotel.management.application.dto.auth.AuthenticationResponseDto;
import com.hotel.management.application.dto.auth.ChangePasswordDto;
import com.hotel.management.application.dto.auth.RegisterRequestDto;
import com.hotel.management.application.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Exposes sign in and sign up REST APIs.")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(description = "REST API to register a new user to the system.", summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody @Valid RegisterRequestDto request) {
        System.out.println(request);
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @Operation(description = "REST API to log in as a user to the system.", summary = "Log in as a user")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody @Valid AuthenticationRequestDto request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @Operation(description = "REST API to generate a new JSON Web Token for a registered user.", summary = "Generate a new JSON Web Token for a registered user")
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @Operation(description = "REST API to change the password of a logged in user.", summary = "Change the password of a logged in user")
    @PatchMapping("/change-password")
    public HttpServletResponse changePassword(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        authenticationService.changePassword(request, response, changePasswordDto);
        return response;
    }

    @Operation(description = "REST API to log out logged in user.", summary = "Log out logged in user")
    @PatchMapping("/logout")
    public HttpServletResponse logout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request);
        return response;
    }
}