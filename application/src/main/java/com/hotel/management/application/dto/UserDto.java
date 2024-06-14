package com.hotel.management.application.dto;

import com.hotel.management.application.entity.user.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private String id;
    private Role role;

    private String firstName, lastName;

    @NotNull
    private String email, password;
}
