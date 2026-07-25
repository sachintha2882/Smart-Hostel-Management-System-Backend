package com.smart.HostalManagementSystem.DTO;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BuildingRequestDTO {


    private String buildingName;

    private String description;

    private Long hostelId;

    private Integer numberOfFloors;

    private Integer roomsPerFloor;

    private Integer roomCapacity;
}