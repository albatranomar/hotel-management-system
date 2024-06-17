package com.hotel.management.application.controller;

import com.hotel.management.application.dto.FacilityDto;
import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.dto.validation.OnUpdate;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("api/v1/facilities")
@Tag(name = "Facility", description = "Exposes APIs to manage room facilities and all their details.")
public class FacilityController {
    private final FacilityService facilityService;

    @Operation(description = "REST API to retrieve all facilities.", summary = "Retrieve all facilities")
    @GetMapping({"", "/"})
    public ResponseEntity<List<FacilityDto>> getAllFacilities() {
        List<FacilityDto> facilities = facilityService.getAllFacilities();
        facilities.forEach(FacilityController::addLinkToDto);

        return ResponseEntity.ok().body(facilities);
    }

    @Operation(description = "REST API to add a new facility to the system.", summary = "Add a new facility")
    @PostMapping
    public ResponseEntity<FacilityDto> createFacility(@Validated(OnCreate.class) @RequestBody FacilityDto facilityDto) {
        FacilityDto createdFacility = facilityService.createFacility(facilityDto);
        addLinkToDto(createdFacility);
        return ResponseEntity.ok().body(createdFacility);
    }

    @Operation(description = "REST API to retrieve a facility by its ID.", summary = "Retrieve a facility")
    @GetMapping("/{id}")
    public ResponseEntity<FacilityDto> getFacilityById(@PathVariable String id) {
        FacilityDto facility = facilityService.getFacilityById(id);
        addLinkToDto(facility);
        return ResponseEntity.ok().body(facility);
    }

    @Operation(description = "REST API to update a facility by its ID.", summary = "Update a facility")
    @PutMapping("/{id}")
    public ResponseEntity<FacilityDto> updateFacility(@PathVariable String id, @RequestBody @Validated(OnUpdate.class) FacilityDto facilityDto) {
        FacilityDto updatedFacility = facilityService.updateFacilityById(id, facilityDto);
        addLinkToDto(updatedFacility);
        return ResponseEntity.ok().body(updatedFacility);
    }

    @Operation(description = "REST API to delete a facility by its ID.", summary = "Delete a facility")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFacility(@PathVariable String id) {
        if (!facilityService.facilityExistsWithId(id))
            throw new ResourceNotFoundException("Facility with id " + id + " does not exist");

        facilityService.deleteFacilityById(id);
        return ResponseEntity.ok().body("Facility deleted successfully");
    }

    public static void addLinkToDto(FacilityDto facilityDto) {
        facilityDto.add(linkTo(methodOn(FacilityController.class).getFacilityById(facilityDto.getId())).withSelfRel());
        facilityDto.add(linkTo(methodOn(FacilityController.class).updateFacility(facilityDto.getId(), null)).withRel("update"));
        facilityDto.add(linkTo(methodOn(FacilityController.class).deleteFacility(facilityDto.getId())).withRel("delete"));
    }
}
