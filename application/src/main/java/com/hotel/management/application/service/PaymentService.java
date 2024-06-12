package com.hotel.management.application.service;

import com.hotel.management.application.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    PaymentDto getPayment(String id);

    List<PaymentDto> getAllPayments();

    PaymentDto addPayment(PaymentDto paymentDto);

    PaymentDto updatePayment(PaymentDto paymentDto);

    void deletePayment(String id);
}
