package com.smart.HostalManagementSystem.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class FloorResponseDTO {

    private Long id;

    private String floorName;

    private Integer floorNumber;

    private List<RoomResponseDTO> rooms;

}