package com.hotel.management.application.repository;

import com.hotel.management.application.entity.Booking;
import org.springframework.data.repository.CrudRepository;

public interface BookingRepository extends CrudRepository<Booking, String> {
}
