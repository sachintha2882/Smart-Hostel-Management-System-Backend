package com.smart.HostalManagementSystem.DTO;


import com.smart.HostalManagementSystem.Enums.HostelType;
import com.smart.HostalManagementSystem.Enums.Status;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HostelResponseDTO {


    private Long id;

    private String hostelName;

    private HostelType hostelType;

    private String location;

    private Integer totalCapacity;

    private Status status;

}