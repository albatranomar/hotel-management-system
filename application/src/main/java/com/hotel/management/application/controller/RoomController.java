package com.hotel.management.application.controller;

import com.hotel.management.application.dto.HouseKeepingDto;
import com.hotel.management.application.dto.RoomDto;
import com.hotel.management.application.dto.validation.OnCreate;
import com.hotel.management.application.dto.validation.OnUpdate;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/v1/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoomController {
    private final RoomService roomService;

    @GetMapping({"/", ""})
    public ResponseEntity<List<RoomDto>> getAllRooms(@RequestParam MultiValueMap<String, String> options) {
        AtomicReference<List<RoomDto>> rooms = new AtomicReference<>(roomService.getAllRooms());

        if (options != null) options.forEach((option, values) -> {
            switch (option.toLowerCase()) {
                case "filter" -> values.forEach(value -> {
                    String[] key_value = value.split(":");
                    if (key_value.length == 2) {
                        String key = key_value[0].toLowerCase();
                        if (key.equals("status"))
                            rooms.set(rooms.get().stream().filter(roomDto -> roomDto.getStatus().name().toLowerCase().contains(key_value[1].toLowerCase())).toList());
                    }
                });
                case "limit" -> {
                    int limit;
                    if (values.isEmpty())
                        limit = 100;
                    else {
                        try {
                            limit = Integer.parseInt(values.get(0));
                        } catch (Exception e) {
                            limit = 100;
                        }
                    }
                    rooms.set(rooms.get().subList(0, Math.min(limit, rooms.get().size())));
                }
            }
        });

        rooms.get().forEach(RoomController::addLinkToDto);

        return ResponseEntity.ok().body(rooms.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable("id") String id) {
        RoomDto roomDto = roomService.getRoomById(id);
        addLinkToDto(roomDto);
        return ResponseEntity.ok().body(roomDto);
    }

    @PostMapping({"/", ""})
    public ResponseEntity<RoomDto> createRoom(@Validated(OnCreate.class) @RequestBody RoomDto roomDto) {
        RoomDto newRoom = roomService.createRoom(roomDto);
        addLinkToDto(newRoom);
        return ResponseEntity.ok().body(newRoom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable("id") String id,
                                              @Validated(OnUpdate.class) @RequestBody RoomDto roomDto) {
        RoomDto updatedRoom = roomService.updateRoomById(id, roomDto);
        addLinkToDto(updatedRoom);
        return ResponseEntity.ok().body(updatedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable("id") String id) {
        if (!roomService.roomExistsWithId(id))
            throw new ResourceNotFoundException("Room with id " + id + " does not exist");

        roomService.deleteRoomById(id);
        return ResponseEntity.ok().body("Room deleted.");
    }

    @GetMapping("/{id}/task")
    public ResponseEntity<List<HouseKeepingDto>> getRoomTasks(@PathVariable("id") String id) {
        List<HouseKeepingDto> tasks = roomService.getRoomTask(id);
        tasks.forEach(HouseKeepingController::addLinkToDto);

        return ResponseEntity.ok().body(tasks);
    }

    @PostMapping("/{roomId}/features/{featureId}")
    public ResponseEntity<String> addFeatureToRoom(@PathVariable("roomId") String roomId, @PathVariable("featureId") String featureId) {
        roomService.addFeatureToRoom(roomId, featureId);
        return ResponseEntity.ok().body("Feature added to room.");
    }

    @DeleteMapping("/{roomId}/features/{featureId}")
    public ResponseEntity<String> removeFeatureFromRoom(@PathVariable("roomId") String roomId, @PathVariable("featureId") String featureId) {
        roomService.removeFeatureFromRoom(roomId, featureId);
        return ResponseEntity.ok().body("Feature removed from room.");
    }

    @PostMapping("/{roomId}/facilities/{facilityId}")
    public ResponseEntity<String> addFacilityToRoom(@PathVariable("roomId") String roomId, @PathVariable("facilityId") String facilityId) {
        roomService.addFacilityToRoom(roomId, facilityId);
        return ResponseEntity.ok().body("Facility added to room.");
    }

    @DeleteMapping("/{roomId}/facilities/{facilityId}")
    public ResponseEntity<String> removeFacilityFromRoom(@PathVariable("roomId") String roomId, @PathVariable("facilityId") String facilityId) {
        roomService.removeFacilityFromRoom(roomId, facilityId);
        return ResponseEntity.ok().body("Facility removed from room.");
    }

    public static void addLinkToDto(RoomDto roomDto) {
        roomDto.add(linkTo(methodOn(RoomController.class).getRoomById(roomDto.getId())).withSelfRel());
        roomDto.add(linkTo(methodOn(RoomController.class).getRoomTasks(roomDto.getId())).withRel("tasks"));
        roomDto.add(linkTo(methodOn(RoomController.class).updateRoom(roomDto.getId(), null)).withRel("update"));
        roomDto.add(linkTo(methodOn(RoomController.class).deleteRoom(roomDto.getId())).withRel("delete"));
        roomDto.add(linkTo(methodOn(RoomController.class).addFeatureToRoom(roomDto.getId(), null)).withRel("add-feature"));
        roomDto.add(linkTo(methodOn(RoomController.class).removeFeatureFromRoom(roomDto.getId(), null)).withRel("remove-feature"));
        roomDto.add(linkTo(methodOn(RoomController.class).addFacilityToRoom(roomDto.getId(), null)).withRel("add-facility"));
        roomDto.add(linkTo(methodOn(RoomController.class).removeFacilityFromRoom(roomDto.getId(), null)).withRel("remove-facility"));
        roomDto.getFeatures().forEach(FeatureController::addLinkToDto);
        roomDto.getFacilities().forEach(FacilityController::addLinkToDto);
    }
}
