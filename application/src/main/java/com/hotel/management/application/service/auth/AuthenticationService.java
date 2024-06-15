package com.hotel.management.application.service.auth;

import com.hotel.management.application.dto.auth.AuthenticationRequestDto;
import com.hotel.management.application.dto.auth.AuthenticationResponseDto;
import com.hotel.management.application.dto.auth.ChangePasswordDto;
import com.hotel.management.application.dto.auth.RegisterRequestDto;
import com.hotel.management.application.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponseDto register(RegisterRequestDto request);
    AuthenticationResponseDto authenticate(AuthenticationRequestDto request);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
    void changePassword(HttpServletRequest request, HttpServletResponse response, ChangePasswordDto changePasswordDto) throws IOException;

    void logout(HttpServletRequest request);

    User getUser(HttpServletRequest request);
}
