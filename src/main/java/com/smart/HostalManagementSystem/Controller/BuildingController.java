package com.smart.HostalManagementSystem.Controller;


import com.smart.HostalManagementSystem.DTO.BuildingRequestDTO;
import com.smart.HostalManagementSystem.DTO.BuildingResponseDTO;
import com.smart.HostalManagementSystem.Service.BuildingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/buildings")
@CrossOrigin
public class BuildingController {


    private final BuildingService buildingService;


    public BuildingController(
            BuildingService buildingService
    ) {

        this.buildingService = buildingService;

    }



    // CREATE BUILDING
    @PostMapping
    public ResponseEntity<BuildingResponseDTO> createBuilding(
            @RequestBody BuildingRequestDTO dto
    ) {


        BuildingResponseDTO response =
                buildingService.createBuilding(dto);


        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );

    }




    // GET ALL BUILDINGS
    @GetMapping
    public ResponseEntity<List<BuildingResponseDTO>> getAllBuildings() {


        List<BuildingResponseDTO> buildings =
                buildingService.getAllBuildings();


        return ResponseEntity.ok(buildings);

    }





    // GET BUILDING BY ID
    @GetMapping("/{id}")
    public ResponseEntity<BuildingResponseDTO> getBuildingById(
            @PathVariable Long id
    ) {


        BuildingResponseDTO building =
                buildingService.getBuildingById(id);


        return ResponseEntity.ok(building);

    }





    // UPDATE BUILDING
    @PutMapping("/{id}")
    public ResponseEntity<BuildingResponseDTO> updateBuilding(
            @PathVariable Long id,
            @RequestBody BuildingRequestDTO dto
    ) {


        BuildingResponseDTO updatedBuilding =
                buildingService.updateBuilding(id, dto);


        return ResponseEntity.ok(updatedBuilding);

    }





    // DELETE BUILDING
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBuilding(
            @PathVariable Long id
    ) {


        buildingService.deleteBuilding(id);


        return ResponseEntity.ok(
                "Building deleted successfully"
        );

    }

}