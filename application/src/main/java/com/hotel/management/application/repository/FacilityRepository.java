package com.hotel.management.application.repository;

import com.hotel.management.application.entity.Facility;
import org.springframework.data.repository.CrudRepository;

public interface FacilityRepository extends CrudRepository<Facility, String> {
}
