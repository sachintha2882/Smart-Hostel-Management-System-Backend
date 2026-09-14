package com.smart.HostalManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceComplaintResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String subWardenRemarks;
    private String maintenanceRemarks;
    private String photoUrl;
    private LocalDateTime completedAt;

    private Long roomId;
    private String roomNumber;
    private String buildingName;
    private Long hostelId;
    private String hostelName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
