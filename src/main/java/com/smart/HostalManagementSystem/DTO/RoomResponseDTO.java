package com.smart.HostalManagementSystem.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;
@Data
@Getter
@Setter
public class RoomResponseDTO {

    private Long id;

    private String roomNumber;

    private Integer capacity;

    private Integer currentOccupancy;

    private Long floorId;

    private String floorName;

}
