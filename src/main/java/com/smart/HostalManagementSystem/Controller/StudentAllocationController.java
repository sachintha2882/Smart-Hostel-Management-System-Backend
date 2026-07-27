package com.smart.HostalManagementSystem.Controller;


import com.smart.HostalManagementSystem.DTO.StudentAllocationRequestDTO;
import com.smart.HostalManagementSystem.DTO.StudentAllocationResponseDTO;
import com.smart.HostalManagementSystem.Service.StudentAllocationService;
import org.springframework.web.bind.annotation.*;
import com.smart.HostalManagementSystem.DTO.BulkAllocationResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/allocations")
@CrossOrigin
public class StudentAllocationController {


    private final StudentAllocationService allocationService;


    public StudentAllocationController(
            StudentAllocationService allocationService) {

        this.allocationService = allocationService;

    }




    // Create Allocation
    @PostMapping
    public StudentAllocationResponseDTO createAllocation(
            @RequestBody StudentAllocationRequestDTO dto) {


        return allocationService.createAllocation(dto);

    }

    @PostMapping("/bulk-upload")
    public BulkAllocationResultDTO bulkUpload(
            @RequestParam("file")  MultipartFile file,
            @RequestParam("floorId") Long floorId,
            @RequestParam("academicYear") String academicYear
    ) throws Exception {
        return allocationService.bulkAllocateFromExcel(file, floorId, academicYear);
    }





    // Get All Allocations
    @GetMapping
    public List<StudentAllocationResponseDTO> getAllAllocations() {


        return allocationService.getAllAllocations();

    }





    // Get Allocation By ID
    @GetMapping("/{id}")
    public StudentAllocationResponseDTO getAllocationById(
            @PathVariable Long id) {


        return allocationService.getAllocationById(id);

    }





    // Get Allocations By Student
    @GetMapping("/student/{studentId}")
    public List<StudentAllocationResponseDTO> getByStudent(
            @PathVariable Long studentId) {


        return allocationService
                .getAllocationsByStudent(studentId);

    }





    // Get Room Members
    @GetMapping("/room/{roomId}")
    public List<StudentAllocationResponseDTO> getRoomMembers(
            @PathVariable Long roomId) {


        return allocationService
                .getRoomMembers(roomId);

    }


    @PutMapping("/{id}/release")
    public StudentAllocationResponseDTO releaseAllocation(
            @PathVariable Long id){

        return allocationService.releaseAllocation(id);

    }


    // Delete Allocation
    @DeleteMapping("/{id}")
    public String deleteAllocation(
            @PathVariable Long id) {


        allocationService.deleteAllocation(id);


        return "Allocation deleted successfully";

    }

}