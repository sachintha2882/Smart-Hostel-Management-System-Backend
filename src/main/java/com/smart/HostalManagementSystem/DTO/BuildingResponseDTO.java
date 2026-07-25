package com.smart.HostalManagementSystem.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class BuildingResponseDTO {


    private Long id;

    private String buildingName;

    private String description;

    private Long hostelId;

    private String hostelName;

}