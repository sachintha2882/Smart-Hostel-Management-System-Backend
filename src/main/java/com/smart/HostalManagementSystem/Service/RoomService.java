package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.DTO.RoomRequestDTO;
import com.smart.HostalManagementSystem.DTO.RoomResponseDTO;
import com.smart.HostalManagementSystem.Entity.Floor;
import com.smart.HostalManagementSystem.Entity.Room;
import com.smart.HostalManagementSystem.Repository.FloorRepository;
import com.smart.HostalManagementSystem.Repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {


    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;


    public RoomService(RoomRepository roomRepository,
                       FloorRepository floorRepository) {

        this.roomRepository = roomRepository;
        this.floorRepository = floorRepository;
    }



    // Create Room
    public RoomResponseDTO createRoom(RoomRequestDTO dto) {


        Floor floor = floorRepository.findById(dto.getFloorId())
                .orElseThrow(() -> new RuntimeException("Floor not found"));


        if(roomRepository.existsByRoomNumberAndFloorId(
                dto.getRoomNumber(),
                dto.getFloorId())) {

            throw new RuntimeException("Room already exists in this floor");
        }


        Room room = new Room();

        room.setRoomNumber(dto.getRoomNumber());
        room.setCapacity(dto.getCapacity());
        room.setCurrentOccupancy(0);
        room.setFloor(floor);


        Room savedRoom = roomRepository.save(room);


        return convertToResponseDTO(savedRoom);
    }



    // Get All Rooms
    public List<RoomResponseDTO> getAllRooms() {


        return roomRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

    }



    // Get Room By ID
    public RoomResponseDTO getRoomById(Long id) {


        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));


        return convertToResponseDTO(room);

    }



    // Update Room
    public RoomResponseDTO updateRoom(Long id,
                                      RoomRequestDTO dto) {


        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));


        Floor floor = floorRepository.findById(dto.getFloorId())
                .orElseThrow(() -> new RuntimeException("Floor not found"));


        room.setRoomNumber(dto.getRoomNumber());
        room.setCapacity(dto.getCapacity());
        room.setFloor(floor);


        Room updatedRoom = roomRepository.save(room);


        return convertToResponseDTO(updatedRoom);

    }



    // Delete Room
    public void deleteRoom(Long id) {

        roomRepository.deleteById(id);

    }



    // Get Rooms By Floor
    public List<RoomResponseDTO> getRoomsByFloor(Long floorId) {


        return roomRepository.findByFloorId(floorId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

    }



    private RoomResponseDTO convertToResponseDTO(Room room) {


        RoomResponseDTO dto = new RoomResponseDTO();

        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setCapacity(room.getCapacity());
        dto.setCurrentOccupancy(room.getCurrentOccupancy());

        dto.setFloorId(room.getFloor().getId());
        dto.setFloorName(room.getFloor().getFloorName());


        return dto;
    }

}