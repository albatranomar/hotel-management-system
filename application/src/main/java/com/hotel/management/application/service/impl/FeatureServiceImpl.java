package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.FeatureDto;
import com.hotel.management.application.entity.Feature;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.FeatureRepository;
import com.hotel.management.application.service.FeatureService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureServiceImpl implements FeatureService {
    private final FeatureRepository featureRepository;

    public FeatureServiceImpl(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    @Override
    public FeatureDto getFeatureById(String id) {
        Feature feature = featureRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feature", "id", id));

        return mapToDto(feature);
    }

    @Override
    public List<FeatureDto> getAllFeatures() {
        List<Feature> features = featureRepository.findAll();
        return features.stream().map(FeatureServiceImpl::mapToDto).toList();
    }

    @Override
    public FeatureDto createFeature(FeatureDto featureDto) {
        return mapToDto(featureRepository.save(mapToEntity(featureDto)));
    }

    @Override
    public FeatureDto updateFeatureById(String id, FeatureDto featureDto) {
        Feature feature = featureRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feature", "id", id));
        try {
            feature.setFname(featureDto.getFname());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid value provided.");
        }

        return mapToDto(featureRepository.save(feature));
    }

    @Override
    public void deleteFeatureById(String id) {
        featureRepository.deleteById(id);
    }

    @Override
    public boolean featureExistsWithId(String id) {
        return featureRepository.existsById(id);
    }

    public static FeatureDto mapToDto(Feature feature) {
        if (feature == null) return null;

        return new FeatureDto(feature.getId(), feature.getFname());
    }

    public static Feature mapToEntity(FeatureDto featureDto) {
        if (featureDto == null) return null;

        return new Feature(featureDto.getId(), featureDto.getFname());
    }
}
