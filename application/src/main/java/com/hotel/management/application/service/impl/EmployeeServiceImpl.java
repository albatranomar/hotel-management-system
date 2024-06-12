package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.EmployeeDto;
import com.hotel.management.application.entity.Employee;
import com.hotel.management.application.entity.HouseKeeping;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.EmployeeRepository;
import com.hotel.management.application.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeDto getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        return mapToDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(this::mapToDto).toList();
    }

    @Override
    public EmployeeDto addEmployee(EmployeeDto employeeDto) {
        return mapToDto(employeeRepository.save(mapToEntity(employeeDto)));
    }

    @Override
    public EmployeeDto updateEmployee(String id, EmployeeDto employeeDto) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        try {
            employee.setFname(employeeDto.getFname());
            employee.setLname(employeeDto.getLname());
            employee.setEmail(employeeDto.getEmail());
            employee.setPhoneNo(employeeDto.getPhoneNo());
            employee.setSalary(employeeDto.getSalary());
            employee.setDateOfBirth(employeeDto.getDateOfBirth());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid value provided.");
        }

        return mapToDto(employeeRepository.save(employee));
    }

    @Override
    public void deleteEmployee(String id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public List<HouseKeeping> getEmployeeTasks(String id) {
        return employeeRepository.findHouseKeepingByEmployeeId(id);
    }

    private EmployeeDto mapToDto(Employee employee) {
        if (employee == null) return null;
        return new EmployeeDto(employee.getId(), employee.getFname(), employee.getLname(), employee.getPhoneNo(), employee.getEmail(), employee.getDateOfBirth(), employee.getSalary());
    }

    private Employee mapToEntity(EmployeeDto employeeDto) {
        if (employeeDto == null) return null;
        return new Employee(employeeDto.getId(), employeeDto.getFname(), employeeDto.getLname(), employeeDto.getPhoneNo(), employeeDto.getEmail(), employeeDto.getDateOfBirth(), employeeDto.getSalary());
    }
}
