package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.DTO.FloorDTO;
import com.smart.HostalManagementSystem.Entity.Building;
import com.smart.HostalManagementSystem.Entity.Floor;
import com.smart.HostalManagementSystem.Entity.Building;
import com.smart.HostalManagementSystem.Repository.BuildingRepository;
import com.smart.HostalManagementSystem.Repository.FloorRepository;
import com.smart.HostalManagementSystem.Repository.BuildingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FloorService {


    private final FloorRepository floorRepository;
    private final BuildingRepository buildingRepository;


    public FloorService(
            FloorRepository floorRepository,
            BuildingRepository buildingRepository
    ){
        this.floorRepository = floorRepository;
        this.buildingRepository = buildingRepository;
    }



    public FloorDTO createFloor(FloorDTO dto){

        Building building = buildingRepository
                .findById(dto.getBuildingId())
                .orElseThrow(() -> new RuntimeException("Building not found"));

        Floor floor = new Floor();

        floor.setFloorName(dto.getFloorName());
        floor.setFloorNumber(dto.getFloorNumber());


        floor.setBuilding(building);


        Floor savedFloor = floorRepository.save(floor);


        return convertToDTO(savedFloor);
    }



    public List<FloorDTO> getAllFloors(){

        return floorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    public FloorDTO getFloorById(Long id){

        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floor not found"));

        return convertToDTO(floor);
    }



    public FloorDTO updateFloor(Long id, FloorDTO dto){


        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floor not found"));


        floor.setFloorName(dto.getFloorName());
        floor.setFloorNumber(dto.getFloorNumber());


        Floor updated = floorRepository.save(floor);


        return convertToDTO(updated);
    }



    public void deleteFloor(Long id){

        floorRepository.deleteById(id);

    }



    private FloorDTO convertToDTO(Floor floor){

        FloorDTO dto = new FloorDTO();

        dto.setId(floor.getId());
        dto.setFloorName(floor.getFloorName());
        dto.setFloorNumber(floor.getFloorNumber());
        dto.setBuildingId(floor.getBuilding().getId());

        return dto;
    }

}