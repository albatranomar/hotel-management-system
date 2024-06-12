package com.hotel.management.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentDto {
    private String id;
    private String paymentStatus;

    private BookingDto booking;
}
