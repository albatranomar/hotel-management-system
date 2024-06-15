package com.hotel.management.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.dto.validation.OnUpdate;
import com.hotel.management.application.entity.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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

    @NotBlank(groups = OnCreate.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    @Builder.Default
    private Role role = Role.CUSTOMER;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String firstName, lastName;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotBlank(groups = OnCreate.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    private String password;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String phoneNumber;
}
