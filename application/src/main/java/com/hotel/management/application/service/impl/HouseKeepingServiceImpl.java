package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.HouseKeepingDto;
import com.hotel.management.application.entity.HouseKeeping;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.HouseKeepingRepository;
import com.hotel.management.application.service.HouseKeepingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseKeepingServiceImpl implements HouseKeepingService {
    private final HouseKeepingRepository houseKeepingRepository;

    public HouseKeepingServiceImpl(HouseKeepingRepository houseKeepingRepository) {
        this.houseKeepingRepository = houseKeepingRepository;
    }

    @Override
    public HouseKeepingDto getHouseKeepingById(String id) {
        HouseKeeping houseKeeping =
                houseKeepingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("HouseKeeping",
                        "id", id));
        return mapToDto(houseKeeping);
    }

    @Override
    public List<HouseKeepingDto> getAllHouseKeepings() {
        List<HouseKeeping> houseKeepingList = houseKeepingRepository.findAll();
        return houseKeepingList.stream().map(HouseKeepingServiceImpl::mapToDto).toList();
    }

    @Override
    public HouseKeepingDto addHouseKeeping(HouseKeepingDto houseKeepingDto) {
        return mapToDto(houseKeepingRepository.save(mapToEntity(houseKeepingDto)));
    }

    @Override
    public HouseKeepingDto updateHouseKeeping(String id, HouseKeepingDto houseKeepingDto) {
        HouseKeeping houseKeeping =
                houseKeepingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("HouseKeeping",
                        "id",
                        id));

        try {
            houseKeeping.setTask(houseKeepingDto.getTask());
            houseKeeping.setStatus(houseKeepingDto.getStatus());
            houseKeeping.setDate(houseKeepingDto.getDate());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid value provided.");
        }

        return mapToDto(houseKeepingRepository.save(houseKeeping));
    }

    @Override
    public void deleteHouseKeepingById(String id) {
        houseKeepingRepository.deleteById(id);
    }

    public static HouseKeepingDto mapToDto(HouseKeeping houseKeeping) {
        if (houseKeeping == null) return null;

        return new HouseKeepingDto(houseKeeping.getId(), houseKeeping.getTask(), houseKeeping.getStatus(),
                houseKeeping.getDate(), EmployeeServiceImpl.mapToDto(houseKeeping.getEmployee()),
                RoomServiceImpl.mapToDto(houseKeeping.getRoom()));
    }

    public static HouseKeeping mapToEntity(HouseKeepingDto houseKeepingDto) {
        if (houseKeepingDto == null) return null;

        return new HouseKeeping(houseKeepingDto.getId(),
                EmployeeServiceImpl.mapToEntity(houseKeepingDto.getEmployee()),
                RoomServiceImpl.mapToEntity(houseKeepingDto.getRoom()), houseKeepingDto.getTask(),
                houseKeepingDto.getStatus(), houseKeepingDto.getDate());
    }
}
