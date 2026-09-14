package com.smart.HostalManagementSystem.DTO;

import com.smart.HostalManagementSystem.Entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponseDTO {

    private Long id;

    private String title;

    private String message;

    private Announcement.TargetType targetType;

    private Long hostelId;

    private String hostelName;

    private String createdBy;

    private String createdByRole;

    private LocalDateTime createdAt;
}