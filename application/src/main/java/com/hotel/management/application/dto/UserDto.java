package com.hotel.management.application.dto;

import com.hotel.management.application.entity.User.Roles;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class UserDto {
    private String id;
    private Roles role;

    private String firstName, lastName;

    @NotNull
    private String email, password;
}
