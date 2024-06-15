package com.hotel.management.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.entity.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
public class UserDto extends RepresentationModel<UserDto> {
    private String id;

    @JsonIgnore
    @Builder.Default
    private Role role = Role.CUSTOMER;

    @NotBlank(groups = OnCreate.class)
    private String firstName, lastName;

    @NotBlank(groups = OnCreate.class)
    private String email;

    @NotBlank(groups = OnCreate.class)
    @JsonIgnore
    private String password;

    @NotBlank(groups = OnCreate.class)
    private String phoneNumber;
}
