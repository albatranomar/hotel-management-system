package com.hotel.management.application.controller;

import com.hotel.management.application.dto.auth.AuthenticationRequestDto;
import com.hotel.management.application.dto.auth.AuthenticationResponseDto;
import com.hotel.management.application.dto.auth.ChangePasswordDto;
import com.hotel.management.application.dto.auth.RegisterRequestDto;
import com.hotel.management.application.service.auth.AuthenticationService;
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
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody @Valid RegisterRequestDto request) {
        System.out.println(request);
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody @Valid AuthenticationRequestDto request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PatchMapping("/change-password")
    public HttpServletResponse changePassword(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody ChangePasswordDto changePasswordDto) throws IOException {
        authenticationService.changePassword(request, response, changePasswordDto);
        return response;
    }

    @PatchMapping("/logout")
    public HttpServletResponse logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.logout(request);
        return response;
    }
}