package com.smart.HostalManagementSystem.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAllocationRequestDTO {


    private Long studentId;

    private Long roomId;

    private String status;

}