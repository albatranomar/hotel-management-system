package com.hotel.management.application.repository;

import com.hotel.management.application.entity.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, String> {
}
