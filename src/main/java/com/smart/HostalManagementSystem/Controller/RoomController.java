package com.smart.HostalManagementSystem.Controller;


import com.smart.HostalManagementSystem.DTO.RoomRequestDTO;
import com.smart.HostalManagementSystem.DTO.RoomResponseDTO;
import com.smart.HostalManagementSystem.Service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/rooms")
@CrossOrigin
public class RoomController {


    private final RoomService roomService;


    public RoomController(RoomService roomService) {

        this.roomService = roomService;

    }



    @PostMapping
    public RoomResponseDTO createRoom(
            @RequestBody RoomRequestDTO dto) {

        return roomService.createRoom(dto);

    }



    @GetMapping
    public List<RoomResponseDTO> getAllRooms() {

        return roomService.getAllRooms();

    }



    @GetMapping("/{id}")
    public RoomResponseDTO getRoomById(
            @PathVariable Long id) {

        return roomService.getRoomById(id);

    }



    @PutMapping("/{id}")
    public RoomResponseDTO updateRoom(
            @PathVariable Long id,
            @RequestBody RoomRequestDTO dto) {

        return roomService.updateRoom(id, dto);

    }



    @DeleteMapping("/{id}")
    public String deleteRoom(
            @PathVariable Long id) {

        roomService.deleteRoom(id);

        return "Room deleted successfully";

    }



    @GetMapping("/floor/{floorId}")
    public List<RoomResponseDTO> getRoomsByFloor(
            @PathVariable Long floorId) {

        return roomService.getRoomsByFloor(floorId);

    }

}