package com.hotel.management.application.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Roles role;

    private String firstName, lastName;

    @NotNull
    private String email, password;

    public static enum Roles {
        ADMIN,
        CUSTOMER
    }
}
