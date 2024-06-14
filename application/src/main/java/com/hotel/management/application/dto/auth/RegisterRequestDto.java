package com.hotel.management.application.dto.auth;

import com.hotel.management.application.entity.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
}