package com.hotel.management.application.controller;

import com.hotel.management.application.dto.EmployeeDto;
import com.hotel.management.application.dto.HouseKeepingDto;
import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping({"/", ""})
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<EmployeeDto> employees = employeeService.getAllEmployees();

        if (employees.size() == 0) throw new ResourceNotFoundException("There is currently no employees");

        employees.forEach(EmployeeController::addLinksToEmployeeDto);

        return ResponseEntity.ok().body(employees);
    }

    @PostMapping({"/", ""})
    public ResponseEntity<EmployeeDto> addEmployee(@RequestBody @Validated(OnCreate.class) EmployeeDto employeeDto) {
        if (employeeService.existsWithEmail(employeeDto.getEmail()))
            throw new BadRequestException("An Employee with email[" + employeeDto.getEmail() + "] already exists");

        EmployeeDto employee = employeeService.addEmployee(employeeDto);

        addLinksToEmployeeDto(employee);

        return ResponseEntity.ok().body(employee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable(name = "id") String id) {
        if (!employeeService.existsWithId(id)) throw new ResourceNotFoundException("Employee with specified id(" + id + ") not found");

        EmployeeDto employeeDto = employeeService.getEmployeeById(id);

        addLinksToEmployeeDto(employeeDto);

        return ResponseEntity.ok().body(employeeDto);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<HouseKeepingDto>> getEmployeeTasksById(@PathVariable(name = "id") String id) {
        if (!employeeService.existsWithId(id)) throw new ResourceNotFoundException("Employee with specified id(" + id + ") not found");

        List<HouseKeepingDto> tasks = employeeService.getEmployeeTasks(id);

        tasks.forEach(HouseKeepingController::addLinkToDto);

        return ResponseEntity.ok().body(tasks);
    }

    @PostMapping("/{empId}/tasks/{taskId}")
    public ResponseEntity<List<HouseKeepingDto>> addNewTaskToEmployee(@PathVariable(name = "empId") String empId, @PathVariable(name = "taskId") String taskId) {
        if (!employeeService.existsWithId(empId)) throw new ResourceNotFoundException("Employee with specified id(" + empId + ") not found");

        employeeService.addTask(empId, taskId);

        List<HouseKeepingDto> tasks = employeeService.getEmployeeTasks(empId);

        tasks.forEach(HouseKeepingController::addLinkToDto);

        return ResponseEntity.ok().body(tasks);
    }

    @DeleteMapping("/{empId}/tasks/{taskId}")
    public ResponseEntity<List<HouseKeepingDto>> deleteTaskFromEmployee(@PathVariable(name = "empId") String empId, @PathVariable(name = "taskId") String taskId) {
        if (!employeeService.existsWithId(empId)) throw new ResourceNotFoundException("Employee with specified id(" + empId + ") not found");

        employeeService.removeTask(empId, taskId);

        List<HouseKeepingDto> tasks = employeeService.getEmployeeTasks(empId);

        tasks.forEach(HouseKeepingController::addLinkToDto);

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

    public static void addLinksToEmployeeDto(EmployeeDto employee) {
        employee.add(linkTo(methodOn(EmployeeController.class).getEmployeeById(employee.getId())).withSelfRel());
        employee.add(linkTo(methodOn(EmployeeController.class).getEmployeeTasksById(employee.getId())).withRel("tasks"));
        employee.add(linkTo(methodOn(EmployeeController.class).updateEmployeeById(employee.getId(), null)).withRel("update"));
        employee.add(linkTo(methodOn(EmployeeController.class).deleteEmployeeById(employee.getId())).withRel("delete"));
        employee.add(linkTo(methodOn(EmployeeController.class).addNewTaskToEmployee(employee.getId(), null)).withRel("add-task"));
        employee.add(linkTo(methodOn(EmployeeController.class).deleteTaskFromEmployee(employee.getId(), null)).withRel("remove-task"));
    }
}
