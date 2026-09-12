package com.smart.HostalManagementSystem.Controller;

import com.smart.HostalManagementSystem.DTO.ComplaintRequestDTO;
import com.smart.HostalManagementSystem.DTO.ComplaintResponseDTO;
import com.smart.HostalManagementSystem.DTO.MaintenanceComplaintResponseDTO;
import com.smart.HostalManagementSystem.DTO.MaintenanceCompletionRequestDTO;
import com.smart.HostalManagementSystem.DTO.SubWardenComplaintResponseDTO;
import com.smart.HostalManagementSystem.Service.ComplaintService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }


    // =========================================================
    // STUDENT: Submit a Complaint (infers student/room from auth)
    // Supports both JSON (no photo) and Multipart (with or without photo)
    // =========================================================
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ComplaintResponseDTO createComplaintJson(
            Authentication authentication,
            @RequestBody ComplaintRequestDTO dto) {
        return complaintService.createComplaintFromAuth(authentication, dto, null);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ComplaintResponseDTO createComplaintMultipart(
            Authentication authentication,
            @RequestPart(value = "complaint", required = false) ComplaintRequestDTO dtoPart,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        ComplaintRequestDTO dto = dtoPart;
        if (dto == null) {
            dto = new ComplaintRequestDTO(title, description, category);
        }
        return complaintService.createComplaintFromAuth(authentication, dto, photo);
    }

    // =========================================================
    // STUDENT: Get own complaints
    // =========================================================
    @GetMapping("/my-complaints")
    public List<ComplaintResponseDTO> getMyComplaints(Authentication authentication) {
        return complaintService.getComplaintsByAuthenticatedStudent(authentication);
    }

    // =========================================================
    // SUB WARDEN: Get complaints for assigned hostel
    // =========================================================
    @GetMapping("/subwarden")
    public List<SubWardenComplaintResponseDTO> getSubWardenComplaints(Authentication authentication) {
        return complaintService.getSubWardenScopedComplaints(authentication);
    }

    // =========================================================
    // SUB WARDEN: Forward Complaint to Maintenance
    // =========================================================
    @PutMapping("/{id}/forward")
    public SubWardenComplaintResponseDTO forwardComplaint(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks,
            Authentication authentication) {
        return complaintService.forwardComplaint(id, remarks, authentication);
    }

    // =========================================================
    // SUB WARDEN: Decline Complaint
    // =========================================================
    @PutMapping("/{id}/decline")
    public SubWardenComplaintResponseDTO declineComplaint(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks,
            Authentication authentication) {
        return complaintService.declineComplaint(id, remarks, authentication);
    }

    // =========================================================
    // MAINTENANCE: Get Work Queue
    // =========================================================
    @GetMapping("/maintenance/queue")
    public List<MaintenanceComplaintResponseDTO> getMaintenanceQueue(Authentication authentication) {
        // The service already handles deriving the hostel from authentication if applicable
        // Or we can just let it get all maintenance complaints if they are not hostel-scoped
        // We'll let the service layer handle any necessary auth derivation if we want it strictly scoped
        // but for now, we'll just return the queue.
        return complaintService.getMaintenanceQueue();
    }

    // =========================================================
    // MAINTENANCE: Get History
    // =========================================================
    @GetMapping("/maintenance/history")
    public List<MaintenanceComplaintResponseDTO> getMaintenanceHistory(Authentication authentication) {
        return complaintService.getMaintenanceHistory();
    }

    // =========================================================
    // MAINTENANCE: Start Work (FORWARDED -> IN_PROGRESS)
    // =========================================================
    @PutMapping("/{id}/start")
    public MaintenanceComplaintResponseDTO startMaintenanceWork(
            @PathVariable Long id,
            Authentication authentication) {
        return complaintService.startMaintenanceWork(id, authentication);
    }

    // =========================================================
    // MAINTENANCE: Complete Work (FORWARDED/IN_PROGRESS -> RESOLVED)
    // =========================================================
    @PutMapping("/{id}/complete")
    public ComplaintResponseDTO completeComplaint(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody MaintenanceCompletionRequestDTO dto) {
        return complaintService.completeComplaint(id, dto.getRemarks());
    }

    // =========================================================
    // ADMIN / GENERAL: Get All Complaints
    // =========================================================
    @GetMapping
    public List<ComplaintResponseDTO> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    // =========================================================
    // Get Complaint By ID
    // =========================================================
    @GetMapping("/{id}")
    public ComplaintResponseDTO getComplaintById(
            @PathVariable Long id,
            Authentication authentication) {
        return complaintService.getComplaintById(id, authentication);
    }

    // =========================================================
    // Get Complaints By Student
    // =========================================================
    @GetMapping("/student/{studentId}")
    public List<ComplaintResponseDTO> getComplaintsByStudent(
            @PathVariable Long studentId) {
        return complaintService.getComplaintsByStudent(studentId);
    }

    // =========================================================
    // Get Complaints By Status
    // =========================================================
    @GetMapping("/status/{status}")
    public List<ComplaintResponseDTO> getComplaintsByStatus(
            @PathVariable String status) {
        return complaintService.getComplaintsByStatus(status);
    }

    // =========================================================
    // Delete Complaint
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint(
            @PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.ok("Complaint deleted successfully");
    }
}
