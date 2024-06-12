package com.hotel.management.application.service;

import com.hotel.management.application.dto.HouseKeepingDto;

import java.util.List;

public interface HouseKeepingService {
    HouseKeepingDto getHouseKeepingById(String id);

    List<HouseKeepingDto> getAllHouseKeepings();

    HouseKeepingDto addHouseKeeping(HouseKeepingDto houseKeepingDto);

    HouseKeepingDto updateHouseKeeping(String id, HouseKeepingDto houseKeepingDto);

    void deleteHouseKeepingById(String id);
}
