package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.RoomDto;
import com.hotel.management.application.entity.Room;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.RoomRepository;
import com.hotel.management.application.service.RoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public RoomDto getRoomById(String id) {
        Room room = roomRepository.findById(id).orElse(null);

        return mapToDto(room);
    }

    @Override
    public List<RoomDto> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();

        return rooms.stream().map(this::mapToDto).toList();
    }

    @Override
    public RoomDto updateRoomById(String id, RoomDto roomDto) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        room.setNo(roomDto.getNo());
        room.setCost(roomDto.getCost());
        room.setCapacity(roomDto.getCapacity());
        room.setStatus(roomDto.getStatus());
        room.setType(roomDto.getType());

        return mapToDto(roomRepository.save(room));
    }

    @Override
    public void deleteRoomById(String id) {
        roomRepository.deleteById(id);
    }

    @Override
    public RoomDto createRoom(RoomDto roomDto) {
        return mapToDto(roomRepository.save(mapToEntity(roomDto)));
    }

    @Override
    public boolean roomExistsWithId(String id) {
        return roomRepository.existsById(id);
    }

    public RoomDto mapToDto(Room room) {
        if (room == null) return null;

        return new RoomDto(
                room.getId(),
                room.getNo(),
                room.getType(),
                room.getStatus(),
                room.getCapacity(),
                room.getCost(),
                room.getFeatures().stream().map(FeatureServiceImpl::mapToDto).toList(),
                room.getFacilities().stream().map(FacilityServiceImpl::mapToDto).toList()
        );
    }

    public Room mapToEntity(RoomDto roomDto) {
        if (roomDto == null) return null;

        if (roomRepository.existsById(roomDto.getId())) {
            return roomRepository.findById(roomDto.getId()).orElse(null);
        }

        return new Room(
                roomDto.getId(),
                roomDto.getNo(),
                roomDto.getType(),
                roomDto.getStatus(),
                roomDto.getCapacity(),
                roomDto.getCost(),
                roomDto.getFeatures().stream().map(FeatureServiceImpl::mapToEntity).toList(),
                roomDto.getFacilities().stream().map(FacilityServiceImpl::mapToEntity).toList()
        );
    }
}
