package com.hotel.management.application.service;

import com.hotel.management.application.dto.HouseKeepingDto;
import com.hotel.management.application.dto.RoomDto;

import java.util.List;

public interface RoomService {
    List<RoomDto> getAllRooms();
    RoomDto getRoomById(String id);
    RoomDto updateRoomById(String id, RoomDto roomDto);
    void deleteRoomById(String id);
    RoomDto createRoom(RoomDto roomDto);
    boolean roomExistsWithId(String id);
    List<HouseKeepingDto> getRoomTask(String id);
}
