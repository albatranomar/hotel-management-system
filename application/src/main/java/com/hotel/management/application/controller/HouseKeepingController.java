package com.hotel.management.application.controller;

import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.dto.validation.OnUpdate;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.HouseKeepingService;
import com.hotel.management.application.dto.HouseKeepingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "House Keeping Task", description = "Exposes APIs to manage house keeping tasks and all their details.")
public class HouseKeepingController {
    private final HouseKeepingService houseKeepingService;

    @Operation(description = "REST API to retrieve all housekeeping tasks in the system.", summary = "Retrieve all housekeeping tasks")
    @GetMapping({"/", ""})
    public ResponseEntity<List<HouseKeepingDto>> getAllTasks() {
        List<HouseKeepingDto> tasks = houseKeepingService.getAllHouseKeepings();
        tasks.forEach(HouseKeepingController::addLinkToDto);

        return ResponseEntity.ok().body(tasks);
    }

    @Operation(description = "REST API retrieve a housekeeping task by its ID.", summary = "Retrieve a housekeeping task")
    @GetMapping("/{id}")
    public ResponseEntity<HouseKeepingDto> getTaskById(@PathVariable("id") String id) {
        HouseKeepingDto task = houseKeepingService.getHouseKeepingById(id);
        addLinkToDto(task);
        return ResponseEntity.ok().body(task);
    }

    @Operation(description = "REST API to add a new housekeeping task to the system.", summary = "Add a new housekeeping task")
    @PostMapping
    public ResponseEntity<HouseKeepingDto> createTask(@RequestBody @Validated(OnCreate.class) HouseKeepingDto task) {
        HouseKeepingDto createTask = houseKeepingService.addHouseKeeping(task);
        addLinkToDto(createTask);
        return ResponseEntity.ok().body(createTask);
    }

    @Operation(description = "REST API to update the details of a housekeeping task by its ID.", summary = "Update the details of a housekeeping task")
    @PutMapping("/{id}")
    public ResponseEntity<HouseKeepingDto> updateTask(@PathVariable String id, @RequestBody @Validated(OnUpdate.class) HouseKeepingDto houseKeepingDto) {
        HouseKeepingDto updatedTask = houseKeepingService.updateHouseKeeping(id, houseKeepingDto);
        addLinkToDto(updatedTask);
        return ResponseEntity.ok().body(updatedTask);
    }

    @Operation(description = "REST API to delete a task by its ID.", summary = "Delete a task")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable String id) {
        if (!houseKeepingService.taskExistsWithId(id))
            throw new ResourceNotFoundException("HouseKeeping with id " + id + " does not exist");

        houseKeepingService.deleteHouseKeepingById(id);
        return ResponseEntity.ok().body("Task deleted successfully");
    }

    public static void addLinkToDto(HouseKeepingDto houseKeepingDto) {
        houseKeepingDto.add(linkTo(methodOn(HouseKeepingController.class).getTaskById(houseKeepingDto.getId())).withSelfRel());
        houseKeepingDto.add(linkTo(methodOn(HouseKeepingController.class).updateTask(houseKeepingDto.getId(), null)).withRel("update"));
        houseKeepingDto.add(linkTo(methodOn(HouseKeepingController.class).deleteTask(houseKeepingDto.getId())).withRel("delete"));
    }
}
