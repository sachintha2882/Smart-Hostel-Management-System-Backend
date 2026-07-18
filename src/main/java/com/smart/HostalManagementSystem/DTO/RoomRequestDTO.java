package com.smart.HostalManagementSystem.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RoomRequestDTO {

    private String roomNumber;

    private Integer capacity;

    private Long floorId;
}
