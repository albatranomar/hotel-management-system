package com.hotel.management.application.repository;

import com.hotel.management.application.entity.HouseKeeping;
import org.springframework.data.repository.CrudRepository;

public interface HouseKeepingRepository extends CrudRepository<HouseKeeping, String> {
}
