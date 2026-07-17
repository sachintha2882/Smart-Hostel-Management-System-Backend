package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.DTO.FloorDTO;
import com.smart.HostalManagementSystem.Entity.Floor;
import com.smart.HostalManagementSystem.Entity.Hostel;
import com.smart.HostalManagementSystem.Repository.FloorRepository;
import com.smart.HostalManagementSystem.Repository.HostelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FloorService {


    private final FloorRepository floorRepository;
    private final HostelRepository hostelRepository;


    public FloorService(
            FloorRepository floorRepository,
            HostelRepository hostelRepository
    ){
        this.floorRepository = floorRepository;
        this.hostelRepository = hostelRepository;
    }



    public FloorDTO createFloor(FloorDTO dto){


        Hostel hostel = hostelRepository.findById(dto.getHostelId())
                .orElseThrow(() -> new RuntimeException("Hostel not found"));


        Floor floor = new Floor();

        floor.setFloorName(dto.getFloorName());
        floor.setFloorNumber(dto.getFloorNumber());
        floor.setHostel(hostel);


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
        dto.setHostelId(floor.getHostel().getId());

        return dto;
    }

}