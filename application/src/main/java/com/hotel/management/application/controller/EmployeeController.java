package com.hotel.management.application.controller;

import com.hotel.management.application.dto.EmployeeDto;
import com.hotel.management.application.dto.HouseKeepingDto;
import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.entity.Employee;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.EmployeeService;
import com.hotel.management.application.service.HouseKeepingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/employees")
@PreAuthorize("hasRole('ADMIN')")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final HouseKeepingService houseKeepingService;

    @GetMapping({"/", ""})
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<EmployeeDto> employees = employeeService.getAllEmployees();

        if (employees.size() == 0) throw new ResourceNotFoundException("There is currently no employees");

        employees.forEach(employeeDto -> {
            employeeDto.add(linkTo(methodOn(EmployeeController.class).getEmployeeById(employeeDto.getId())).withSelfRel());
            employeeDto.add(linkTo(methodOn(EmployeeController.class).getEmployeeTasksById(employeeDto.getId())).withRel("tasks"));
            employeeDto.add(linkTo(methodOn(EmployeeController.class).updateEmployeeById(employeeDto.getId(), null)).withRel("update"));
            employeeDto.add(linkTo(methodOn(EmployeeController.class).deleteEmployeeById(employeeDto.getId())).withRel("delete"));
        });

        return ResponseEntity.ok().body(employees);
    }

    @PostMapping({"/", ""})
    public ResponseEntity<EmployeeDto> addEmployee(@RequestBody @Validated(OnCreate.class) EmployeeDto employeeDto) {
        if (employeeService.existsWithEmail(employeeDto.getEmail()))
            throw new BadRequestException("An Employee with email[" + employeeDto.getEmail() + "] already exists");

        EmployeeDto employee = employeeService.addEmployee(employeeDto);

        employee.add(linkTo(methodOn(EmployeeController.class).getEmployeeById(employee.getId())).withSelfRel());
        employee.add(linkTo(methodOn(EmployeeController.class).getEmployeeTasksById(employee.getId())).withRel("tasks"));
        employee.add(linkTo(methodOn(EmployeeController.class).updateEmployeeById(employee.getId(), null)).withRel("update"));
        employee.add(linkTo(methodOn(EmployeeController.class).deleteEmployeeById(employee.getId())).withRel("delete"));

        return ResponseEntity.ok().body(employee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable(name = "id") String id) {
        if (!employeeService.existsWithId(id)) throw new ResourceNotFoundException("Employee with specified id(" + id + ") not found");

        EmployeeDto employeeDto = employeeService.getEmployeeById(id);

        employeeDto.add(linkTo(methodOn(EmployeeController.class).getEmployeeById(id)).withSelfRel());
        employeeDto.add(linkTo(methodOn(EmployeeController.class).getEmployeeTasksById(id)).withRel("tasks"));
        employeeDto.add(linkTo(methodOn(EmployeeController.class).updateEmployeeById(employeeDto.getId(), null)).withRel("update"));
        employeeDto.add(linkTo(methodOn(EmployeeController.class).deleteEmployeeById(employeeDto.getId())).withRel("delete"));

        return ResponseEntity.ok().body(employeeDto);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<HouseKeepingDto>> getEmployeeTasksById(@PathVariable(name = "id") String id) {
        if (!employeeService.existsWithId(id)) throw new ResourceNotFoundException("Employee with specified id(" + id + ") not found");

        List<HouseKeepingDto> tasks = employeeService.getEmployeeTasks(id);

        return ResponseEntity.ok().body(tasks);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<EmployeeDto> updateEmployeeById(@PathVariable(name = "id") String id, @RequestBody @Validated(OnCreate.class) EmployeeDto employeeDto) {
        if (!employeeService.existsWithId(id)) throw new ResourceNotFoundException("Employee with specified id(" + id + ") not found");

        EmployeeDto employee = employeeService.updateEmployee(id, employeeDto);

        employee.add(linkTo(methodOn(EmployeeController.class).getEmployeeById(id)).withSelfRel());
        employee.add(linkTo(methodOn(EmployeeController.class).getEmployeeTasksById(id)).withRel("tasks"));
        employee.add(linkTo(methodOn(EmployeeController.class).updateEmployeeById(employee.getId(), null)).withRel("update"));
        employee.add(linkTo(methodOn(EmployeeController.class).deleteEmployeeById(employee.getId())).withRel("delete"));

        return ResponseEntity.ok().body(employee);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Object> deleteEmployeeById(@PathVariable(name = "id") String id) {
        if (!employeeService.existsWithId(id))
            throw new ResourceNotFoundException("Employee with specified id(" + id + ") not found");

        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().body("The employee was successfully deleted");
    }
}
