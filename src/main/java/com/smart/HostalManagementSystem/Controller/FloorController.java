package com.smart.HostalManagementSystem.Controller;


import com.smart.HostalManagementSystem.DTO.FloorDTO;
import com.smart.HostalManagementSystem.Service.FloorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/floors")
@CrossOrigin
public class FloorController {


    private final FloorService floorService;


    public FloorController(FloorService floorService){
        this.floorService = floorService;
    }



    @PostMapping
    public FloorDTO createFloor(@RequestBody FloorDTO dto){

        return floorService.createFloor(dto);

    }



    @GetMapping
    public List<FloorDTO> getAllFloors(){

        return floorService.getAllFloors();

    }



    @GetMapping("/{id}")
    public FloorDTO getFloor(@PathVariable Long id){

        return floorService.getFloorById(id);

    }



    @PutMapping("/{id}")
    public FloorDTO updateFloor(
            @PathVariable Long id,
            @RequestBody FloorDTO dto){

        return floorService.updateFloor(id,dto);

    }



    @DeleteMapping("/{id}")
    public String deleteFloor(@PathVariable Long id){

        floorService.deleteFloor(id);

        return "Floor deleted successfully";

    }

}