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
public class ComplaintResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String subWardenRemarks;
    private String maintenanceRemarks;
    private LocalDateTime completedAt;
    private Long studentId;
    private String studentName;
    private String studentIndexNumber;
    private Long roomId;
    private String roomNumber;
    private Long hostelId;
    private String hostelName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
