package com.hotel.management.application.service;

import com.hotel.management.application.dto.FeatureDto;

import java.util.List;

public interface FeatureService {
    List<FeatureDto> getAllFeatures();
    FeatureDto getFeatureById(String id);
    FeatureDto updateFeatureById(String id, FeatureDto featureDto);
    void deleteFeatureById(String id);
    FeatureDto createFeature(FeatureDto featureDto);
    boolean featureExistsWithId(String id);
}
