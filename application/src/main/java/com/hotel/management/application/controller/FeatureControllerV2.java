package com.hotel.management.application.controller;

import com.hotel.management.application.dto.FeatureDto;
import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.service.FeatureService;
import lombok.AllArgsConstructor;
import lombok.Data;
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
@RequestMapping("api")
public class FeatureControllerV2 {
    private final FeatureService featureService;

    @GetMapping({"/v2/features", "/v2/features/"})
    public ResponseEntity<ResponseObject> getAllFeaturesV2() {
        List<FeatureDto> features = featureService.getAllFeatures();
        features.forEach(FeatureController::addLinkToDto);

        return ResponseEntity.ok().body(new ResponseObject("version 2 URI path", features));
    }

    @PostMapping(value = {"/features", "/features/"}, params = "version=3")
    public ResponseEntity<ResponseObject> createFeatureParamV3(@Validated(OnCreate.class) @RequestBody FeatureDto featureDto) {
        FeatureDto createdFeature = featureService.createFeature(featureDto);
        addLinkToDto(createdFeature);
        return ResponseEntity.ok().body(new ResponseObject("version 3 using param", createdFeature));
    }

    @GetMapping(value = "/features/{id}", headers = "X-API-VERSION=4")
    public ResponseEntity<ResponseObject> getFeatureByIdHeaderV4(@PathVariable String id) {
        FeatureDto featureDto = featureService.getFeatureById(id);
        addLinkToDto(featureDto);
        return ResponseEntity.ok().body(new ResponseObject("version 4 using headers", featureDto));
    }

    public static void addLinkToDto(FeatureDto featureDto) {
        featureDto.add(linkTo(methodOn(FeatureController.class).getFeatureById(featureDto.getId())).withSelfRel());
        featureDto.add(linkTo(methodOn(FeatureController.class).updateFeature(featureDto.getId(), null)).withRel("update"));
        featureDto.add(linkTo(methodOn(FeatureController.class).deleteFeature(featureDto.getId())).withRel("delete"));
    }

    @Data
    @AllArgsConstructor
    private static class ResponseObject {
        String version;
        Object data;
    }
}
