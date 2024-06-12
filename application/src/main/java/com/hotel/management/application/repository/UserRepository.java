package com.hotel.management.application.repository;

import com.hotel.management.application.entity.Booking;
import com.hotel.management.application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);

    @Query("SELECT b FROM Booking b WHERE b.customer = ?1")
    List<Booking> getBookings(String userId);
}