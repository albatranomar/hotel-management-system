package com.hotel.management.application.repository;

import com.hotel.management.application.entity.HouseKeeping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseKeepingRepository extends JpaRepository<HouseKeeping, String> {
}
