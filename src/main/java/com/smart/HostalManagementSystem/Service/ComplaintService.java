package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.DTO.ComplaintRequestDTO;
import com.smart.HostalManagementSystem.DTO.ComplaintResponseDTO;
import com.smart.HostalManagementSystem.Entity.Complaint;
import com.smart.HostalManagementSystem.Entity.Room;
import com.smart.HostalManagementSystem.Entity.Student;
import com.smart.HostalManagementSystem.Repository.ComplaintRepository;
import com.smart.HostalManagementSystem.Repository.RoomRepository;
import com.smart.HostalManagementSystem.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;

    public ComplaintService(ComplaintRepository complaintRepository,
                            StudentRepository studentRepository,
                            RoomRepository roomRepository) {
        this.complaintRepository = complaintRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
    }


    // Submit a Complaint (by Student)
    public ComplaintResponseDTO createComplaint(ComplaintRequestDTO dto) {

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Complaint complaint = new Complaint();
        complaint.setTitle(dto.getTitle());
        complaint.setDescription(dto.getDescription());
        complaint.setCategory(dto.getCategory());
        complaint.setStatus("PENDING");
        complaint.setStudent(student);
        complaint.setRoom(room);

        Complaint saved = complaintRepository.save(complaint);
        return convertToResponseDTO(saved);
    }


    // Get All Complaints
    public List<ComplaintResponseDTO> getAllComplaints() {
        return complaintRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    // Get Complaint By ID
    public ComplaintResponseDTO getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        return convertToResponseDTO(complaint);
    }


    // Get Complaints By Student
    public List<ComplaintResponseDTO> getComplaintsByStudent(Long studentId) {
        return complaintRepository.findByStudentId(studentId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    // Get Complaints By Status
    public List<ComplaintResponseDTO> getComplaintsByStatus(String status) {
        return complaintRepository.findByStatus(status)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    // Forward Complaint to Maintenance (Sub Warden action)
    public ComplaintResponseDTO forwardComplaint(Long id, String subWardenRemarks) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus("FORWARDED");
        complaint.setSubWardenRemarks(subWardenRemarks);

        Complaint updated = complaintRepository.save(complaint);
        return convertToResponseDTO(updated);
    }


    // Decline Complaint (Sub Warden action)
    public ComplaintResponseDTO declineComplaint(Long id, String subWardenRemarks) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus("DECLINED");
        complaint.setSubWardenRemarks(subWardenRemarks);

        Complaint updated = complaintRepository.save(complaint);
        return convertToResponseDTO(updated);
    }


    // Resolve Complaint
    public ComplaintResponseDTO resolveComplaint(Long id) {
        return completeComplaint(id, "Marked as resolved.");
    }

    // Complete a forwarded complaint (Maintenance action)
    public ComplaintResponseDTO completeComplaint(Long id, String maintenanceRemarks) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (!"FORWARDED".equalsIgnoreCase(complaint.getStatus())) {
            throw new IllegalStateException("Only forwarded complaints can be completed by maintenance");
        }

        complaint.setStatus("RESOLVED");
        complaint.setMaintenanceRemarks(maintenanceRemarks);
        complaint.setCompletedAt(LocalDateTime.now());

        Complaint updated = complaintRepository.save(complaint);
        return convertToResponseDTO(updated);
    }

    // Work queue visible to the maintenance team
    public List<ComplaintResponseDTO> getMaintenanceQueue() {
        return complaintRepository.findByStatusIn(List.of("FORWARDED", "IN_PROGRESS"))
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Completed jobs, shown in maintenance history
    public List<ComplaintResponseDTO> getMaintenanceHistory() {
        return complaintRepository.findByStatus("RESOLVED")
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    // Delete Complaint
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }


    private ComplaintResponseDTO convertToResponseDTO(Complaint complaint) {

        ComplaintResponseDTO dto = new ComplaintResponseDTO();
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setCategory(complaint.getCategory());
        dto.setStatus(complaint.getStatus());
        dto.setSubWardenRemarks(complaint.getSubWardenRemarks());
        dto.setMaintenanceRemarks(complaint.getMaintenanceRemarks());
        dto.setCompletedAt(complaint.getCompletedAt());
        dto.setStudentId(complaint.getStudent().getId());
        dto.setStudentName(complaint.getStudent().getFullName());
        dto.setStudentIndexNumber(complaint.getStudent().getRegistrationNumber());
        dto.setRoomId(complaint.getRoom().getId());
        dto.setRoomNumber(complaint.getRoom().getRoomNumber());
        dto.setHostelId(complaint.getRoom().getFloor().getBuilding().getHostel().getId());
        dto.setHostelName(complaint.getRoom().getFloor().getBuilding().getHostel().getHostelName());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());

        return dto;
    }
}
