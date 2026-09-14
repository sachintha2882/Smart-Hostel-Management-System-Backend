package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.DTO.ComplaintRequestDTO;
import com.smart.HostalManagementSystem.DTO.ComplaintResponseDTO;
import com.smart.HostalManagementSystem.DTO.MaintenanceComplaintResponseDTO;
import com.smart.HostalManagementSystem.DTO.SubWardenComplaintResponseDTO;
import com.smart.HostalManagementSystem.Entity.Complaint;
import com.smart.HostalManagementSystem.Entity.Room;
import com.smart.HostalManagementSystem.Entity.Student;
import com.smart.HostalManagementSystem.Entity.StudentAllocation;
import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Repository.ComplaintRepository;
import com.smart.HostalManagementSystem.Repository.RoomRepository;
import com.smart.HostalManagementSystem.Repository.StudentAllocationRepository;
import com.smart.HostalManagementSystem.Repository.StudentRepository;
import com.smart.HostalManagementSystem.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final StudentAllocationRepository allocationRepository;

    @Value("${complaint.upload-dir:uploads/complaints}")
    private String uploadDir;

    private Path uploadPath;

    public ComplaintService(ComplaintRepository complaintRepository,
                            StudentRepository studentRepository,
                            RoomRepository roomRepository,
                            UserRepository userRepository,
                            StudentAllocationRepository allocationRepository) {
        this.complaintRepository = complaintRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.allocationRepository = allocationRepository;
    }

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadPath, e);
        }
    }

    // =========================================================
    // STUDENT: Submit complaint — derives hostel+room from auth
    // =========================================================

    /**
     * Creates a complaint for the authenticated student.
     * studentId and roomId are derived from the active StudentAllocation —
     * NOT trusted from the request body.
     */
    public ComplaintResponseDTO createComplaintFromAuth(Authentication authentication,
                                                        ComplaintRequestDTO dto,
                                                        MultipartFile photo) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (user.getStudent() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can submit complaints");
        }

        Student student = user.getStudent();

        // Derive room from active StudentAllocation
        StudentAllocation activeAllocation = allocationRepository
                .findByStudentId(student.getId())
                .stream()
                .filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "You must have an active room allocation before submitting a complaint"));

        Room room = activeAllocation.getRoom();

        // Validate input
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Complaint title is required");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Complaint description is required");
        }
        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Complaint category is required");
        }

        Complaint complaint = new Complaint();
        complaint.setTitle(dto.getTitle().trim());
        complaint.setDescription(dto.getDescription().trim());
        complaint.setCategory(dto.getCategory().trim());
        complaint.setStatus("PENDING");
        complaint.setStudent(student);
        complaint.setRoom(room);

        // Save photo if provided
        if (photo != null && !photo.isEmpty()) {
            validatePhoto(photo);
            String photoUrl = savePhoto(photo);
            complaint.setPhotoUrl(photoUrl);
        }

        Complaint saved = complaintRepository.save(complaint);
        return convertToStudentResponseDTO(saved);
    }

    // =========================================================
    // STUDENT: Get own complaints
    // =========================================================

    public List<ComplaintResponseDTO> getComplaintsByAuthenticatedStudent(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (user.getStudent() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can access this endpoint");
        }

        return complaintRepository.findByStudentIdOrderByCreatedAtDesc(user.getStudent().getId())
                .stream()
                .map(this::convertToStudentResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================
    // SUB WARDEN: Get hostel-scoped complaints
    // =========================================================

    /**
     * Returns all complaints for the Sub Warden's assigned hostel only.
     * No student PII — uses SubWardenComplaintResponseDTO.
     */
    public List<SubWardenComplaintResponseDTO> getSubWardenScopedComplaints(Authentication authentication) {
        User subWarden = resolveAuthenticatedUser(authentication);
        Long hostelId = resolveSubWardenHostelId(subWarden);

        return complaintRepository
                .findByRoomFloorBuildingHostelIdOrderByCreatedAtDesc(hostelId)
                .stream()
                .map(this::convertToSubWardenResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================
    // SUB WARDEN: Forward complaint
    // =========================================================

    public SubWardenComplaintResponseDTO forwardComplaint(Long id, String subWardenRemarks, Authentication authentication) {
        User subWarden = resolveAuthenticatedUser(authentication);
        Long hostelId = resolveSubWardenHostelId(subWarden);

        Complaint complaint = getComplaintAndVerifyHostel(id, hostelId);

        if (!"PENDING".equalsIgnoreCase(complaint.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only PENDING complaints can be forwarded");
        }

        complaint.setStatus("FORWARDED");
        complaint.setSubWardenRemarks(subWardenRemarks);

        return convertToSubWardenResponseDTO(complaintRepository.save(complaint));
    }

    // =========================================================
    // SUB WARDEN: Decline complaint
    // =========================================================

    public SubWardenComplaintResponseDTO declineComplaint(Long id, String subWardenRemarks, Authentication authentication) {
        User subWarden = resolveAuthenticatedUser(authentication);
        Long hostelId = resolveSubWardenHostelId(subWarden);

        Complaint complaint = getComplaintAndVerifyHostel(id, hostelId);

        if (!"PENDING".equalsIgnoreCase(complaint.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only PENDING complaints can be declined");
        }

        complaint.setStatus("DECLINED");
        complaint.setSubWardenRemarks(subWardenRemarks);

        return convertToSubWardenResponseDTO(complaintRepository.save(complaint));
    }

    // =========================================================
    // MAINTENANCE: Get forwarded complaints queue
    // =========================================================

    public List<MaintenanceComplaintResponseDTO> getMaintenanceQueue() {
        return getMaintenanceQueueByHostel(null);
    }

    public List<MaintenanceComplaintResponseDTO> getMaintenanceQueueByHostel(Long hostelId) {
        List<Complaint> complaints = (hostelId != null)
                ? complaintRepository.findByRoomFloorBuildingHostelIdAndStatusIn(hostelId, List.of("FORWARDED", "IN_PROGRESS"))
                : complaintRepository.findByStatusIn(List.of("FORWARDED", "IN_PROGRESS"));
        return complaints.stream().map(this::convertToMaintenanceResponseDTO).collect(Collectors.toList());
    }

    // =========================================================
    // MAINTENANCE: Start work (FORWARDED → IN_PROGRESS)
    // =========================================================

    public MaintenanceComplaintResponseDTO startMaintenanceWork(Long id, Authentication authentication) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));

        // Maintenance staff hostel check
        if (authentication != null) {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user != null && user.getHostel() != null) {
                Long complaintHostelId = complaint.getRoom().getFloor().getBuilding().getHostel().getId();
                if (!user.getHostel().getId().equals(complaintHostelId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "This complaint does not belong to your hostel");
                }
            }
        }

        if (!"FORWARDED".equalsIgnoreCase(complaint.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only FORWARDED complaints can be started");
        }

        complaint.setStatus("IN_PROGRESS");
        return convertToMaintenanceResponseDTO(complaintRepository.save(complaint));
    }

    // =========================================================
    // MAINTENANCE: Complete work (FORWARDED or IN_PROGRESS → RESOLVED)
    // =========================================================

    public ComplaintResponseDTO completeComplaint(Long id, String maintenanceRemarks) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));

        String status = complaint.getStatus();
        if (!"FORWARDED".equalsIgnoreCase(status) && !"IN_PROGRESS".equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only FORWARDED or IN_PROGRESS complaints can be resolved");
        }

        complaint.setStatus("RESOLVED");
        complaint.setMaintenanceRemarks(maintenanceRemarks);
        complaint.setCompletedAt(LocalDateTime.now());

        Complaint updated = complaintRepository.save(complaint);
        return convertToStudentResponseDTO(updated);
    }

    // =========================================================
    // MAINTENANCE: History
    // =========================================================

    public List<MaintenanceComplaintResponseDTO> getMaintenanceHistory() {
        return getMaintenanceHistoryByHostel(null);
    }

    public List<MaintenanceComplaintResponseDTO> getMaintenanceHistoryByHostel(Long hostelId) {
        List<Complaint> complaints = (hostelId != null)
                ? complaintRepository.findByRoomFloorBuildingHostelIdAndStatus(hostelId, "RESOLVED")
                : complaintRepository.findByStatus("RESOLVED");
        return complaints.stream().map(this::convertToMaintenanceResponseDTO).collect(Collectors.toList());
    }

    // =========================================================
    // ADMIN: Get all complaints / by student / by status / by ID
    // =========================================================

    public List<ComplaintResponseDTO> getAllComplaints() {
        return complaintRepository.findAll()
                .stream()
                .map(this::convertToStudentResponseDTO)
                .collect(Collectors.toList());
    }

    public ComplaintResponseDTO getComplaintById(Long id, Authentication authentication) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));

        if (authentication != null) {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user != null) {
                switch (user.getRole()) {
                    case STUDENT -> {
                        // Students can only view their own complaints
                        if (user.getStudent() == null || !user.getStudent().getId().equals(complaint.getStudent().getId())) {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
                        }
                    }
                    case SUBWARDEN -> {
                        // Sub Wardens can only view complaints from their hostel
                        if (user.getHostel() == null) {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sub Warden has no assigned hostel");
                        }
                        Long complaintHostelId = complaint.getRoom().getFloor().getBuilding().getHostel().getId();
                        if (!user.getHostel().getId().equals(complaintHostelId)) {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                    "This complaint does not belong to your hostel");
                        }
                    }
                    case MAINTENANCE -> {
                        // Maintenance can only view forwarded/in-progress complaints
                        if (!"FORWARDED".equalsIgnoreCase(complaint.getStatus()) &&
                            !"IN_PROGRESS".equalsIgnoreCase(complaint.getStatus()) &&
                            !"RESOLVED".equalsIgnoreCase(complaint.getStatus())) {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
                        }
                        if (user.getHostel() != null) {
                            Long complaintHostelId = complaint.getRoom().getFloor().getBuilding().getHostel().getId();
                            if (!user.getHostel().getId().equals(complaintHostelId)) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "This complaint does not belong to your hostel");
                            }
                        }
                    }
                    // ADMIN and STUDENT_AFFAIRS can access any
                    default -> { }
                }
            }
        }

        return convertToStudentResponseDTO(complaint);
    }

    public List<ComplaintResponseDTO> getComplaintsByStudent(Long studentId) {
        return complaintRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::convertToStudentResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ComplaintResponseDTO> getComplaintsByStatus(String status) {
        return complaintRepository.findByStatus(status)
                .stream()
                .map(this::convertToStudentResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }

    // =========================================================
    // Backward-compat (kept for any callers not yet migrated)
    // =========================================================

    /** @deprecated Use createComplaintFromAuth instead */
    @Deprecated
    public ComplaintResponseDTO createComplaintWithPhoto(ComplaintRequestDTO dto, MultipartFile photo) {
        // Legacy path — requires studentId/roomId via a workaround.
        // Should not be used for production student complaint submission.
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Please use the authenticated complaint submission endpoint");
    }

    public ComplaintResponseDTO resolveComplaint(Long id) {
        return completeComplaint(id, "Marked as resolved by admin.");
    }

    // =========================================================
    // Private helpers
    // =========================================================

    private User resolveAuthenticatedUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Long resolveSubWardenHostelId(User user) {
        if (user.getHostel() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Sub Warden is not assigned to any hostel. Please contact the administrator.");
        }
        return user.getHostel().getId();
    }

    private Complaint getComplaintAndVerifyHostel(Long complaintId, Long expectedHostelId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));

        Long complaintHostelId = complaint.getRoom().getFloor().getBuilding().getHostel().getId();
        if (!complaintHostelId.equals(expectedHostelId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This complaint does not belong to your hostel");
        }
        return complaint;
    }

    private void validatePhoto(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null ||
            (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only JPEG and PNG images are allowed");
        }
        if (file.getSize() > 5L * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Image must be less than 5 MB");
        }
    }

    private String savePhoto(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + extension;
            Path targetPath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/complaints/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save complaint photo", e);
        }
    }

    /**
     * Student-facing response DTO — includes the student's own PII since it's their data.
     */
    private ComplaintResponseDTO convertToStudentResponseDTO(Complaint complaint) {
        ComplaintResponseDTO dto = new ComplaintResponseDTO();
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setCategory(complaint.getCategory());
        dto.setStatus(complaint.getStatus());
        dto.setSubWardenRemarks(complaint.getSubWardenRemarks());
        dto.setMaintenanceRemarks(complaint.getMaintenanceRemarks());
        dto.setPhotoUrl(complaint.getPhotoUrl());
        dto.setCompletedAt(complaint.getCompletedAt());
        dto.setStudentId(complaint.getStudent().getId());
        // These are safe to include since the student is viewing their own data
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

    /**
     * Sub Warden response DTO — NO student PII.
     */
    private SubWardenComplaintResponseDTO convertToSubWardenResponseDTO(Complaint complaint) {
        SubWardenComplaintResponseDTO dto = new SubWardenComplaintResponseDTO();
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setCategory(complaint.getCategory());
        dto.setStatus(complaint.getStatus());
        dto.setSubWardenRemarks(complaint.getSubWardenRemarks());
        dto.setMaintenanceRemarks(complaint.getMaintenanceRemarks());
        dto.setPhotoUrl(complaint.getPhotoUrl());
        dto.setCompletedAt(complaint.getCompletedAt());
        dto.setRoomId(complaint.getRoom().getId());
        dto.setRoomNumber(complaint.getRoom().getRoomNumber());
        dto.setBuildingName(complaint.getRoom().getFloor().getBuilding().getBuildingName());
        dto.setHostelId(complaint.getRoom().getFloor().getBuilding().getHostel().getId());
        dto.setHostelName(complaint.getRoom().getFloor().getBuilding().getHostel().getHostelName());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        return dto;
    }

    /**
     * Maintenance response DTO — NO student PII.
     */
    private MaintenanceComplaintResponseDTO convertToMaintenanceResponseDTO(Complaint complaint) {
        MaintenanceComplaintResponseDTO dto = new MaintenanceComplaintResponseDTO();
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setCategory(complaint.getCategory());
        dto.setStatus(complaint.getStatus());
        dto.setSubWardenRemarks(complaint.getSubWardenRemarks());
        dto.setMaintenanceRemarks(complaint.getMaintenanceRemarks());
        dto.setPhotoUrl(complaint.getPhotoUrl());
        dto.setCompletedAt(complaint.getCompletedAt());
        dto.setRoomId(complaint.getRoom().getId());
        dto.setRoomNumber(complaint.getRoom().getRoomNumber());
        dto.setBuildingName(complaint.getRoom().getFloor().getBuilding().getBuildingName());
        dto.setHostelId(complaint.getRoom().getFloor().getBuilding().getHostel().getId());
        dto.setHostelName(complaint.getRoom().getFloor().getBuilding().getHostel().getHostelName());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        return dto;
    }
}
