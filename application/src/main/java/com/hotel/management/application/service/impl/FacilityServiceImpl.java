package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.FacilityDto;
import com.hotel.management.application.entity.Facility;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.FacilityRepository;
import com.hotel.management.application.service.FacilityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;

    public FacilityServiceImpl(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Override
    public FacilityDto getFacilityById(String id) {
        Facility facility = facilityRepository.findById(id).orElse(null);

        return mapToDto(facility);
    }

    @Override
    public List<FacilityDto> getAllFacilities() {
        List<Facility> facilities = facilityRepository.findAll();
        return facilities.stream().map(this::mapToDto).toList();
    }

    @Override
    public FacilityDto updateFacilityById(String id, FacilityDto facilityDto) {
        Facility facility = facilityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Facility", "id", id));
        try {
            facility.setFname(facility.getFname());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid value provided.");
        }

        return mapToDto(facilityRepository.save(facility));
    }

    @Override
    public void deleteFacilityById(String id) {
        facilityRepository.deleteById(id);
    }

    @Override
    public FacilityDto createFacility(FacilityDto facilityDto) {
        return mapToDto(facilityRepository.save(mapToEntity(facilityDto)));
    }

    @Override
    public boolean facilityExistsWithId(String id) {
        return facilityRepository.existsById(id);
    }

    public FacilityDto mapToDto(Facility facility) {
        if (facility == null) return null;

        return new FacilityDto(facility.getId(), facility.getFname());
    }

    public Facility mapToEntity(FacilityDto facilityDto) {
        if (facilityDto == null) return null;

        return new Facility(facilityDto.getId(), facilityDto.getFname());
    }
}
