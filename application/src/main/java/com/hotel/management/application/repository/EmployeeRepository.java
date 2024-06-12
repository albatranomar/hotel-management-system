package com.hotel.management.application.repository;

import com.hotel.management.application.entity.Employee;
import com.hotel.management.application.entity.HouseKeeping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    @Query("SELECT hk FROM HouseKeeping hk WHERE hk.employee = ?1")
    List<HouseKeeping> findHouseKeepingByEmployeeId(String id);
}
