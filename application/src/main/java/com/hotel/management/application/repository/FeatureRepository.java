package com.hotel.management.application.repository;

import com.hotel.management.application.entity.Feature;
import org.springframework.data.repository.CrudRepository;

public interface FeatureRepository extends CrudRepository<Feature, String> {
}
