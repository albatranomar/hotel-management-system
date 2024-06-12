package com.hotel.management.application.repository;

import com.hotel.management.application.entity.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, String> {
}
