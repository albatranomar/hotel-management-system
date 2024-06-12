package com.hotel.management.application.repository;

import org.springframework.data.repository.CrudRepository;

import com.hotel.management.application.entity.Room;

public interface RoomRepository extends CrudRepository<Room, String> {
}
