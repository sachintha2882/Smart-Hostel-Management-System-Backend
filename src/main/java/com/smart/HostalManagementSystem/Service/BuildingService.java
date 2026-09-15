package com.smart.HostalManagementSystem.Service;



import com.smart.HostalManagementSystem.DTO.BuildingRequestDTO;
import com.smart.HostalManagementSystem.DTO.BuildingResponseDTO;
import com.smart.HostalManagementSystem.Entity.*;
import com.smart.HostalManagementSystem.Repository.*;
import com.smart.HostalManagementSystem.DTO.FloorResponseDTO;
import com.smart.HostalManagementSystem.DTO.RoomResponseDTO;

import  java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {


    private final BuildingRepository buildingRepository;

    private final HostelRepository hostelRepository;

    private final FloorRepository floorRepository;

    private final RoomRepository roomRepository;

    private final HostelService hostelService;



    // CREATE BUILDING + AUTO CREATE FLOOR + ROOM

    public BuildingResponseDTO createBuilding(
            BuildingRequestDTO request
    ){


        Hostel hostel = hostelRepository
                .findById(request.getHostelId())
                .orElseThrow(
                        () -> new RuntimeException("Hostel not found")
                );



        Building building = new Building();

        building.setBuildingName(
                request.getBuildingName()
        );

        building.setDescription(
                request.getDescription()
        );

        building.setHostel(hostel);



        Building savedBuilding =
                buildingRepository.save(building);



        // CREATE FLOORS

        for(int i = 1; i <= request.getNumberOfFloors(); i++){


            Floor floor = new Floor();

            floor.setFloorName(
                    "Floor " + i
            );

            floor.setFloorNumber(i);

            floor.setBuilding(savedBuilding);



            Floor savedFloor =
                    floorRepository.save(floor);



            // CREATE ROOMS

            for(int j = 1; j <= request.getRoomsPerFloor(); j++){


                Room room = new Room();


                String roomNumber =
                        i + String.format("%02d", j);


                room.setRoomNumber(roomNumber);


                room.setCapacity(
                        request.getRoomCapacity()
                );


                room.setCurrentOccupancy(0);


                room.setFloor(savedFloor);



                roomRepository.save(room);


            }

        }

        // All Floors and Rooms are now saved. Recompute the SYSTEM-CONTROLLED
        // Hostel total capacity so it reflects this new Building.
        hostelService.recalculateTotalCapacity(hostel.getId());

        return convertToDTO(savedBuilding);

    }






    // GET ALL BUILDINGS

    public List<BuildingResponseDTO> getAllBuildings(){


        return buildingRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }






    // GET BUILDING BY ID

    public BuildingResponseDTO getBuildingById(Long id){


        Building building =
                buildingRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException("Building not found")
                        );


        return convertToDTO(building);

    }








    // UPDATE BUILDING

    public BuildingResponseDTO updateBuilding(
            Long id,
            BuildingRequestDTO request
    ){


        Building building =
                buildingRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException("Building not found")
                        );



        building.setBuildingName(
                request.getBuildingName()
        );


        building.setDescription(
                request.getDescription()
        );



        Building updated =
                buildingRepository.save(building);

        // Hostel capacity is SYSTEM-CONTROLLED: keep it accurate after edit.
        hostelService.recalculateTotalCapacity(
                building.getHostel().getId()
        );



        return convertToDTO(updated);

    }







    // DELETE BUILDING

    public void deleteBuilding(Long id){


        Building building =
                buildingRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException("Building not found")
                        );


        // Capture the Hostel BEFORE the Building is deleted.
        Long hostelId = building.getHostel().getId();

        buildingRepository.delete(building);

        // Hostel capacity is SYSTEM-CONTROLLED: it must drop after deletion
        // because this Building's Rooms are gone.
        hostelService.recalculateTotalCapacity(hostelId);

    }

    public BuildingResponseDTO getBuildingDetails(Long id) {


        Building building = buildingRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Building not found")
                );


        BuildingResponseDTO response =
                new BuildingResponseDTO();


        response.setId(
                building.getId()
        );


        response.setBuildingName(
                building.getBuildingName()
        );


        response.setDescription(
                building.getDescription()
        );



        response.setFloors(

                building.getFloors()
                        .stream()
                        .map(floor -> {


                            FloorResponseDTO floorDTO =
                                    new FloorResponseDTO();


                            floorDTO.setId(
                                    floor.getId()
                            );


                            floorDTO.setFloorName(
                                    floor.getFloorName()
                            );


                            floorDTO.setFloorNumber(
                                    floor.getFloorNumber()
                            );


                            floorDTO.setRooms(

                                    floor.getRooms()
                                            .stream()
                                            .map(room -> {


                                                RoomResponseDTO roomDTO =
                                                        new RoomResponseDTO();


                                                roomDTO.setId(
                                                        room.getId()
                                                );


                                                roomDTO.setRoomNumber(
                                                        room.getRoomNumber()
                                                );


                                                roomDTO.setCapacity(
                                                        room.getCapacity()
                                                );


                                                roomDTO.setCurrentOccupancy(
                                                        room.getCurrentOccupancy()
                                                );


                                                return roomDTO;


                                            })
                                            .collect(Collectors.toList())

                            );


                            return floorDTO;


                        })
                        .collect(Collectors.toList())

        );


        return response;

    }







    // ENTITY TO DTO CONVERTER

    private BuildingResponseDTO convertToDTO(
            Building building
    ){


        BuildingResponseDTO dto =
                new BuildingResponseDTO();


        dto.setId(
                building.getId()
        );


        dto.setBuildingName(
                building.getBuildingName()
        );


        dto.setDescription(
                building.getDescription()
        );


        dto.setHostelId(
                building.getHostel().getId()
        );


        return dto;

    }



}