package com.hotel.management.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel.management.application.entity.Room;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
}
