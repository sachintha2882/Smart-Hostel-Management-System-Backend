package com.smart.HostalManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response DTO for Sub Warden complaint views.
 * Intentionally does NOT include any student personal information
 * (no studentName, no studentIndexNumber, no studentId).
 * Only location info (hostel, room) and complaint details are exposed.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubWardenComplaintResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String subWardenRemarks;
    private String maintenanceRemarks;
    private String photoUrl;
    private LocalDateTime completedAt;

    // Location info — no student PII
    private Long roomId;
    private String roomNumber;
    private String buildingName;
    private Long hostelId;
    private String hostelName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
