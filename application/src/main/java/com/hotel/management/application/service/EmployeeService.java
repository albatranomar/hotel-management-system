package com.hotel.management.application.service;

import com.hotel.management.application.dto.EmployeeDto;
import com.hotel.management.application.dto.HouseKeepingDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto getEmployeeById(String id);

    List<EmployeeDto> getAllEmployees();

    EmployeeDto addEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(String id, EmployeeDto employeeDto);

    void deleteEmployee(String id);

    List<HouseKeepingDto> getEmployeeTasks(String id);

    boolean existsWithId(String id);

    boolean existsWithEmail(String email);

    void addTask(String empId, String taskId);

    void removeTask(String empId, String taskId);
}
