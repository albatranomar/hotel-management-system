package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.HouseKeepingDto;
import com.hotel.management.application.dto.RoomDto;
import com.hotel.management.application.entity.Facility;
import com.hotel.management.application.entity.Feature;
import com.hotel.management.application.entity.Room;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.FacilityRepository;
import com.hotel.management.application.repository.FeatureRepository;
import com.hotel.management.application.repository.RoomRepository;
import com.hotel.management.application.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final FeatureRepository featureRepository;
    private final FacilityRepository facilityRepository;

    @Override
    public RoomDto getRoomById(String id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));

        return mapToDto(room);
    }

    @Override
    public List<RoomDto> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();

        return rooms.stream().map(RoomServiceImpl::mapToDto).toList();
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

    @Override
    public List<HouseKeepingDto> getRoomTask(String id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        return room.getTasks().stream().map(HouseKeepingServiceImpl::mapToDto).toList();
    }

    @Override
    public void addFeatureToRoom(String roomId, String featureId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        Feature feature = featureRepository.findById(featureId).orElseThrow(() -> new ResourceNotFoundException("Feature", "id", featureId));
        if (room.getFeatures().contains(feature))
            throw new BadRequestException("Room already has this feature.");

        room.getFeatures().add(feature);
        roomRepository.save(room);
    }

    @Override
    public void removeFeatureFromRoom(String roomId, String featureId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        Feature feature = featureRepository.findById(featureId).orElseThrow(() -> new ResourceNotFoundException("Feature", "id", featureId));
        if (!room.getFeatures().contains(feature))
            throw new BadRequestException("Room doesn't have this feature.");

        room.getFeatures().remove(feature);
        roomRepository.save(room);
    }

    @Override
    public void addFacilityToRoom(String roomId, String facilityId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        Facility facility = facilityRepository.findById(facilityId).orElseThrow(() -> new ResourceNotFoundException("Facility", "id", facilityId));
        if (room.getFacilities().contains(facility))
            throw new BadRequestException("Room already has this facility.");

        room.getFacilities().add(facility);
        roomRepository.save(room);
    }

    @Override
    public void removeFacilityFromRoom(String roomId, String facilityId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        Facility facility = facilityRepository.findById(facilityId).orElseThrow(() -> new ResourceNotFoundException("Facility", "id", facilityId));
        if (!room.getFacilities().contains(facility))
            throw new BadRequestException("Room doesn't have this facility.");

        room.getFacilities().remove(facility);
        roomRepository.save(room);
    }

    public static RoomDto mapToDto(Room room) {
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

    public static Room mapToEntity(RoomDto roomDto) {
        if (roomDto == null) return null;

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
