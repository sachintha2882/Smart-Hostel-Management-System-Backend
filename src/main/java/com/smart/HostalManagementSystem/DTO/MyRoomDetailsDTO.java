package com.smart.HostalManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyRoomDetailsDTO {

    // Student's own info
    private String fullName;
    private String registrationNumber;
    private String email;
    private String phoneNumber;
    private String faculty;

    // Hostel hierarchy
    private String hostelName;
    private String buildingName;
    private String floorName;
    private String roomNumber;
    private Integer roomCapacity;
    private Integer currentOccupancy;

    // Allocation info
    private LocalDate allocatedDate;
    private LocalDate expectedReleaseDate;
    private String academicYear;
    private String status;

    // Roommates (same room, ACTIVE students, current student ain karala)
    private List<String> roommateNames;
}