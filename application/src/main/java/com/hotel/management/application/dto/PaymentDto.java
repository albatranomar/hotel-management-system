package com.hotel.management.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentDto {
    private String id;

    @NotBlank
    private String paymentStatus;

    private BookingDto booking;
}
