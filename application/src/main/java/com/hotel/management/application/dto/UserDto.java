package com.hotel.management.application.dto;

import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.entity.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserDto {
    private String id;

    @Builder.Default
    private Role role = Role.CUSTOMER;

    @NotBlank(groups = OnCreate.class)
    private String firstName, lastName;

    @NotBlank(groups = OnCreate.class)
    private String email, password, phoneNumber;
}
