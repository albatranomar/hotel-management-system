package com.hotel.management.application.controller;

import com.hotel.management.application.dto.FeatureDto;
import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.dto.validation.OnUpdate;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.FeatureService;
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
@RequestMapping("api/v1/features")
public class FeatureController {
    private final FeatureService featureService;

    @GetMapping({"", "/"})
    public ResponseEntity<List<FeatureDto>> getAllFeatures() {
        List<FeatureDto> features = featureService.getAllFeatures();
        features.forEach(FeatureController::addLinkToDto);

        return ResponseEntity.ok().body(features);
    }

    @PostMapping
    public ResponseEntity<FeatureDto> createFeature(@Validated(OnCreate.class) @RequestBody FeatureDto featureDto) {
        FeatureDto createdFeature = featureService.createFeature(featureDto);
        addLinkToDto(createdFeature);
        return ResponseEntity.ok().body(createdFeature);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeatureDto> getFeatureById(@PathVariable String id) {
        FeatureDto featureDto = featureService.getFeatureById(id);
        addLinkToDto(featureDto);
        return ResponseEntity.ok().body(featureDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeatureDto> updateFeature(@PathVariable String id, @RequestBody @Validated(OnUpdate.class) FeatureDto featureDto) {
        FeatureDto updatedFeature = featureService.updateFeatureById(id, featureDto);
        addLinkToDto(updatedFeature);
        return ResponseEntity.ok().body(updatedFeature);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeature(@PathVariable String id) {
        if (!featureService.featureExistsWithId(id))
            throw new ResourceNotFoundException("Feature with id " + id + " does not exist");

        featureService.deleteFeatureById(id);
        return ResponseEntity.ok().body("Feature deleted successfully");
    }

    public static void addLinkToDto(FeatureDto featureDto) {
        featureDto.add(linkTo(methodOn(FeatureController.class).getFeatureById(featureDto.getId())).withSelfRel());
        featureDto.add(linkTo(methodOn(FeatureController.class).updateFeature(featureDto.getId(), null)).withRel("update"));
        featureDto.add(linkTo(methodOn(FeatureController.class).deleteFeature(featureDto.getId())).withRel("delete"));
    }
}
