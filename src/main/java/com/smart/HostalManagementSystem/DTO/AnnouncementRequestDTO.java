package com.smart.HostalManagementSystem.DTO;

import com.smart.HostalManagementSystem.Entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequestDTO {

    private String title;

    private String message;

    private Announcement.TargetType targetType;

    private Long hostelId;

    private String category;

    private String priority;
}