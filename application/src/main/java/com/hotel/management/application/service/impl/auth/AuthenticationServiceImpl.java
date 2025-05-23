package com.hotel.management.application.service.impl.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.management.application.dto.auth.AuthenticationRequestDto;
import com.hotel.management.application.dto.auth.AuthenticationResponseDto;
import com.hotel.management.application.dto.auth.ChangePasswordDto;
import com.hotel.management.application.dto.auth.RegisterRequestDto;
import com.hotel.management.application.entity.user.Token;
import com.hotel.management.application.entity.user.User;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.TokenRepository;
import com.hotel.management.application.repository.UserRepository;
import com.hotel.management.application.service.auth.AuthenticationService;
import com.hotel.management.application.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponseDto register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) throw new BadRequestException("User already registered!");

        User user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getUser(request);

        String accessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    @SneakyThrows
    @Override
    public void changePassword(HttpServletRequest request, HttpServletResponse response, ChangePasswordDto changePasswordDto) {
        User user = getUser(request);

        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword()))
            throw new BadRequestException("Wrong password.");

        if (changePasswordDto.getNewPassword().equals(changePasswordDto.getOldPassword()))
            throw new BadRequestException("New password can't match old password.");

        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword()))
            throw new BadRequestException("Password and confirmation password don't match.");

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));

        String newAccessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, newAccessToken);
        var authResponse = AuthenticationResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    @Override
    public void logout(HttpServletRequest request) {
        User user = getUser(request);

        revokeAllUserTokens(user);
    }

    @Override
    public User getUser(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken;
        String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new BadRequestException("Missing Authorization header.");

        accessToken = authHeader.substring(7);
        try {
            userEmail = jwtService.extractUsername(accessToken);
        } catch (Exception e) {
            throw new AccessDeniedException("Invalid access token.");
        }
        if (userEmail == null)
            throw new AccessDeniedException("Invalid access token.");

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        if (!jwtService.isTokenValid(accessToken, user)) return null;

        return user;
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }
}
