package com.smart.HostalManagementSystem.Service;


import com.smart.HostalManagementSystem.DTO.BuildingRequestDTO;
import com.smart.HostalManagementSystem.DTO.BuildingResponseDTO;
import com.smart.HostalManagementSystem.Entity.Building;
import com.smart.HostalManagementSystem.Entity.Hostel;
import com.smart.HostalManagementSystem.Repository.BuildingRepository;
import com.smart.HostalManagementSystem.Repository.HostelRepository;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BuildingService {


    private final BuildingRepository buildingRepository;
    private final HostelRepository hostelRepository;


    public BuildingService(
            BuildingRepository buildingRepository,
            HostelRepository hostelRepository
    ) {

        this.buildingRepository = buildingRepository;
        this.hostelRepository = hostelRepository;

    }



    // CREATE
    public BuildingResponseDTO createBuilding(
            BuildingRequestDTO dto
    ) {


        Hostel hostel = hostelRepository
                .findById(dto.getHostelId())
                .orElseThrow(
                        () -> new RuntimeException("Hostel not found")
                );


        Building building = new Building();


        building.setBuildingName(dto.getBuildingName());

        building.setDescription(dto.getDescription());

        building.setHostel(hostel);


        Building saved = buildingRepository.save(building);


        return mapToDTO(saved);

    }




    // READ ALL
    public List<BuildingResponseDTO> getAllBuildings() {


        return buildingRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();

    }





    // READ BY ID
    public BuildingResponseDTO getBuildingById(Long id) {


        Building building = buildingRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Building not found")
                );


        return mapToDTO(building);

    }





    // UPDATE
    public BuildingResponseDTO updateBuilding(
            Long id,
            BuildingRequestDTO dto
    ) {


        Building building = buildingRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Building not found")
                );



        Hostel hostel = hostelRepository
                .findById(dto.getHostelId())
                .orElseThrow(
                        () -> new RuntimeException("Hostel not found")
                );



        building.setBuildingName(
                dto.getBuildingName()
        );


        building.setDescription(
                dto.getDescription()
        );


        building.setHostel(hostel);



        Building updated =
                buildingRepository.save(building);



        return mapToDTO(updated);

    }





    // DELETE
    public void deleteBuilding(Long id) {


        Building building = buildingRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Building not found")
                );


        buildingRepository.delete(building);

    }





    // ENTITY -> DTO conversion
    private BuildingResponseDTO mapToDTO(
            Building building
    ) {


        return new BuildingResponseDTO(

                building.getId(),

                building.getBuildingName(),

                building.getDescription(),

                building.getHostel().getId(),

                building.getHostel().getHostelName()

        );

    }

}