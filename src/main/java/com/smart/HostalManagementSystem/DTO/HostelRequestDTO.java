package com.smart.HostalManagementSystem.DTO;


import com.smart.HostalManagementSystem.Enums.HostelType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HostelRequestDTO {


    private String hostelName;

    private HostelType hostelType;

    private String location;

    private Integer totalCapacity;

}