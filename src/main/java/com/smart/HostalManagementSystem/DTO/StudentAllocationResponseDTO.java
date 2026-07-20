package com.smart.HostalManagementSystem.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAllocationResponseDTO {


    private Long id;


    private Long studentId;

    private String studentName;

    private String registrationNumber;


    private Long roomId;

    private String roomNumber;


    private LocalDate allocatedDate;


    private LocalDate releasedDate;

    private String status;

}