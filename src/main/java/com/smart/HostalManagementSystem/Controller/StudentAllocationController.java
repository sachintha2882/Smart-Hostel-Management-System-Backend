package com.smart.HostalManagementSystem.Controller;


import com.smart.HostalManagementSystem.DTO.StudentAllocationRequestDTO;
import com.smart.HostalManagementSystem.DTO.StudentAllocationResponseDTO;
import com.smart.HostalManagementSystem.DTO.MyRoomDetailsDTO;
import com.smart.HostalManagementSystem.Service.StudentAllocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smart.HostalManagementSystem.DTO.BulkAllocationResultDTO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import java.time.LocalDate;

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
    public ResponseEntity<?> bulkUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("floorId") Long floorId,
            @RequestParam("academicYear") String academicYear,
            @RequestParam("expectedReleaseDate") String expectedReleaseDate
    ) {

        try {

            BulkAllocationResultDTO result =
                    allocationService.bulkAllocateFromExcel(
                            file,
                            floorId,
                            academicYear,
                            expectedReleaseDate
                    );

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong during upload");

        }
    }

    @GetMapping("/my-room")
    public MyRoomDetailsDTO getMyRoomDetails(Authentication authentication) {
        String username = authentication.getName();
        return allocationService.getMyRoomDetails(username);

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

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {

        allocationService.updateStatus(id, status);

        return ResponseEntity.ok("Status updated successfully");
    }




    // Delete Allocation
    @DeleteMapping("/{id}")
    public String deleteAllocation(
            @PathVariable Long id) {


        allocationService.deleteAllocation(id);


        return "Allocation deleted successfully";

    }

}