package com.smart.HostalManagementSystem.Service;


import com.smart.HostalManagementSystem.DTO.BuildingRequestDTO;
import com.smart.HostalManagementSystem.DTO.BuildingResponseDTO;
import com.smart.HostalManagementSystem.Entity.*;
import com.smart.HostalManagementSystem.Repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BuildingService {


    private final BuildingRepository buildingRepository;

    private final HostelRepository hostelRepository;

    private final FloorRepository floorRepository;

    private final RoomRepository roomRepository;



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



        return convertToDTO(updated);

    }







    // DELETE BUILDING

    public void deleteBuilding(Long id){


        Building building =
                buildingRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException("Building not found")
                        );


        buildingRepository.delete(building);

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