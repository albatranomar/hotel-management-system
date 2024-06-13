package com.hotel.management.application.service;

import com.hotel.management.application.dto.FacilityDto;

import java.util.List;

public interface FacilityService {
    List<FacilityDto> getAllFacilities();
    FacilityDto getFacilityById(String id);
    FacilityDto updateFacilityById(String id, FacilityDto facilityDto);
    void deleteFacilityById(String id);
    FacilityDto createFacility(FacilityDto facilityDto);
    boolean facilityExistsWithId(String id);
}
