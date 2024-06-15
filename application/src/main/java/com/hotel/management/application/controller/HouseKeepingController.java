package com.hotel.management.application.controller;

import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.dto.validation.OnUpdate;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.HouseKeepingService;
import com.hotel.management.application.dto.HouseKeepingDto;
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
public class HouseKeepingController {
    private final HouseKeepingService houseKeepingService;

    @GetMapping({"/", ""})
    public ResponseEntity<List<HouseKeepingDto>> getAllTasks() {
        List<HouseKeepingDto> tasks = houseKeepingService.getAllHouseKeepings();
        tasks.forEach(HouseKeepingController::addLinkToDto);

        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HouseKeepingDto> getTaskById(@PathVariable("id") String id) {
        HouseKeepingDto task = houseKeepingService.getHouseKeepingById(id);
        addLinkToDto(task);
        return ResponseEntity.ok().body(task);
    }

    @PostMapping
    public ResponseEntity<HouseKeepingDto> createTask(@RequestBody @Validated(OnCreate.class) HouseKeepingDto task) {
        HouseKeepingDto createTask = houseKeepingService.addHouseKeeping(task);
        addLinkToDto(task);
        return ResponseEntity.ok().body(createTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HouseKeepingDto> updateTask(@PathVariable String id, @RequestBody @Validated(OnUpdate.class) HouseKeepingDto houseKeepingDto) {
        HouseKeepingDto updatedTask = houseKeepingService.updateHouseKeeping(id, houseKeepingDto);
        addLinkToDto(updatedTask);
        return ResponseEntity.ok().body(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable String id) {
        if (!houseKeepingService.taskExistsWithId(id))
            throw new ResourceNotFoundException("HouseKeeping with id " + id + " does not exist");

        houseKeepingService.deleteHouseKeepingById(id);
        return ResponseEntity.ok().body("Task deleted successfully");
    }

    private static void addLinkToDto(HouseKeepingDto houseKeepingDto) {
        houseKeepingDto.add(linkTo(methodOn(HouseKeepingController.class).getTaskById(houseKeepingDto.getId())).withSelfRel());
        houseKeepingDto.add(linkTo(methodOn(HouseKeepingController.class).updateTask(houseKeepingDto.getId(), null)).withRel("update"));
        houseKeepingDto.add(linkTo(methodOn(HouseKeepingController.class).deleteTask(houseKeepingDto.getId())).withRel("delete"));
    }
}
