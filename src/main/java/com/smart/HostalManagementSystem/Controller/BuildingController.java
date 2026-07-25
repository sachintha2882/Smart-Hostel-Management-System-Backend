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
@CrossOrigin(origins = "http://localhost:5173")
public class BuildingController {


    private final BuildingService buildingService;


    public BuildingController(BuildingService buildingService) {

        this.buildingService = buildingService;

    }



    // CREATE BUILDING + AUTO FLOOR + ROOM CREATION

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


        return ResponseEntity.ok(
                buildingService.getAllBuildings()
        );

    }





    // GET BUILDING BY ID

    @GetMapping("/{id}")
    public ResponseEntity<BuildingResponseDTO> getBuildingById(
            @PathVariable Long id
    ) {


        return ResponseEntity.ok(
                buildingService.getBuildingById(id)
        );

    }





    // UPDATE BUILDING

    @PutMapping("/{id}")
    public ResponseEntity<BuildingResponseDTO> updateBuilding(
            @PathVariable Long id,
            @RequestBody BuildingRequestDTO dto
    ) {


        return ResponseEntity.ok(
                buildingService.updateBuilding(id, dto)
        );

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