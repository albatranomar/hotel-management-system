package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.EmployeeDto;
import com.hotel.management.application.dto.HouseKeepingDto;
import com.hotel.management.application.entity.Employee;
import com.hotel.management.application.entity.HouseKeeping;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.EmployeeRepository;
import com.hotel.management.application.repository.HouseKeepingRepository;
import com.hotel.management.application.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final HouseKeepingRepository houseKeepingRepository;

    @Override
    public EmployeeDto getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        return mapToDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(EmployeeServiceImpl::mapToDto).toList();
    }

    @Override
    public EmployeeDto addEmployee(EmployeeDto employeeDto) {
        return mapToDto(employeeRepository.save(mapToEntity(employeeDto)));
    }

    @Override
    public EmployeeDto updateEmployee(String id, EmployeeDto employeeDto) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee"
                , "id", id));

        try {
            employee.setFname(employeeDto.getFname());
            employee.setLname(employeeDto.getLname());
            employee.setEmail(employeeDto.getEmail());
            employee.setPhoneNo(employeeDto.getPhoneNo());
            employee.setSalary(employeeDto.getSalary());
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
    public List<HouseKeepingDto> getEmployeeTasks(String id) {
        return employeeRepository.findHouseKeepingByEmployeeId(id).stream().map(HouseKeepingServiceImpl::mapToDto).toList();
    }

    @Override
    public boolean existsWithId(String id) {
        return employeeRepository.existsById(id);
    }

    @Override
    public boolean existsWithEmail(String email) {
        return employeeRepository.existsByEmail(email);
    }

    @Override
    public void addTask(String empId, String taskId) {
        Employee employee = employeeRepository.findById(empId).orElseThrow(() -> new ResourceNotFoundException("Employee", "id", empId));
        HouseKeeping task = houseKeepingRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("HouseKeeping", "id", taskId));
        if (employee.getTasks().contains(task))
            throw new BadRequestException("Employee already has this task.");

        employee.getTasks().add(task);
        employeeRepository.save(employee);
    }

    @Override
    public void removeTask(String empId, String taskId) {
        Employee employee = employeeRepository.findById(empId).orElseThrow(() -> new ResourceNotFoundException("Employee", "id", empId));
        HouseKeeping task = houseKeepingRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("HouseKeeping", "id", taskId));
        if (employee.getTasks().contains(task))
            throw new BadRequestException("Employee does not have this task!");

        employee.getTasks().remove(task);
        employeeRepository.save(employee);
    }

    public static EmployeeDto mapToDto(Employee employee) {
        if (employee == null) return null;
        return new EmployeeDto(employee.getId(), employee.getFname(), employee.getLname(), employee.getPhoneNo(),
                employee.getEmail(), employee.getSalary());
    }

    public static Employee mapToEntity(EmployeeDto employeeDto) {
        if (employeeDto == null) return null;
        return new Employee(employeeDto.getId(), employeeDto.getFname(), employeeDto.getLname(),
                employeeDto.getPhoneNo(), employeeDto.getEmail(),
                employeeDto.getSalary());
    }
}
