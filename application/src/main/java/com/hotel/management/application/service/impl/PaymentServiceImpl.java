package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.PaymentDto;
import com.hotel.management.application.entity.Payment;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.PaymentRepository;
import com.hotel.management.application.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentDto getPaymentById(String id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment",
                "id", id));
        return mapToDto(payment);
    }

    @Override
    public List<PaymentDto> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(PaymentServiceImpl::mapToDto).toList();
    }

    @Override
    public PaymentDto addPayment(PaymentDto paymentDto) {
        return mapToDto(paymentRepository.save(mapToEntity(paymentDto)));
    }

    @Override
    public PaymentDto updatePayment(String id, PaymentDto paymentDto) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee"
                , "id", id));

        try {
            payment.setPayment_status(payment.getPayment_status());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid value provided.");
        }

        return mapToDto(paymentRepository.save(payment));
    }

    @Override
    public void deletePayment(String id) {
        paymentRepository.deleteById(id);
    }

    public static PaymentDto mapToDto(Payment payment) {
        return new PaymentDto(payment.getId(), payment.getPayment_status(),
                BookingServiceImpl.mapToDto(payment.getBooking()));
    }

    public static Payment mapToEntity(PaymentDto paymentDto) {
        return new Payment(paymentDto.getId(), paymentDto.getPaymentStatus(),
                BookingServiceImpl.mapToEntity(paymentDto.getBooking()));
    }
}
