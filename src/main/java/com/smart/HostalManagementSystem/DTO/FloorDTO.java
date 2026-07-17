package com.smart.HostalManagementSystem.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FloorDTO {

    private Long id;

    private String floorName;

    private int floorNumber;

    private Long hostelId;
}