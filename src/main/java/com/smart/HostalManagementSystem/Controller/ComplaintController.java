package com.smart.HostalManagementSystem.Controller;

import com.smart.HostalManagementSystem.DTO.ComplaintRequestDTO;
import com.smart.HostalManagementSystem.DTO.ComplaintResponseDTO;
import com.smart.HostalManagementSystem.Service.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }


    // Submit a Complaint (Student)
    @PostMapping
    public ComplaintResponseDTO createComplaint(
            @RequestBody ComplaintRequestDTO dto) {
        return complaintService.createComplaint(dto);
    }


    // Get All Complaints
    @GetMapping
    public List<ComplaintResponseDTO> getAllComplaints() {
        return complaintService.getAllComplaints();
    }


    // Get Complaint By ID
    @GetMapping("/{id}")
    public ComplaintResponseDTO getComplaintById(
            @PathVariable Long id) {
        return complaintService.getComplaintById(id);
    }


    // Get Complaints By Student
    @GetMapping("/student/{studentId}")
    public List<ComplaintResponseDTO> getComplaintsByStudent(
            @PathVariable Long studentId) {
        return complaintService.getComplaintsByStudent(studentId);
    }


    // Get Complaints By Status
    @GetMapping("/status/{status}")
    public List<ComplaintResponseDTO> getComplaintsByStatus(
            @PathVariable String status) {
        return complaintService.getComplaintsByStatus(status);
    }


    // Forward Complaint to Maintenance (Sub Warden)
    @PutMapping("/{id}/forward")
    public ComplaintResponseDTO forwardComplaint(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks) {
        return complaintService.forwardComplaint(id, remarks);
    }


    // Decline Complaint (Sub Warden)
    @PutMapping("/{id}/decline")
    public ComplaintResponseDTO declineComplaint(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks) {
        return complaintService.declineComplaint(id, remarks);
    }


    // Mark Complaint as Resolved
    @PutMapping("/{id}/resolve")
    public ComplaintResponseDTO resolveComplaint(
            @PathVariable Long id) {
        return complaintService.resolveComplaint(id);
    }


    // Delete Complaint
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint(
            @PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.ok("Complaint deleted successfully");
    }
}
