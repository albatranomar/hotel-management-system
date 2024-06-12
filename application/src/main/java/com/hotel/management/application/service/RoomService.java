package com.hotel.management.application.service;

import com.hotel.management.application.dto.RoomDto;

import java.util.List;

public interface RoomService {
    List<RoomDto> getAllRooms();
    RoomDto getRoomById();
    RoomDto updateRoomById(String id, RoomDto bookingDto);
    void deleteProviderById(String id);
    RoomDto createRoom(RoomDto roomDto);
    boolean roomExistsWithId(String id);
}
