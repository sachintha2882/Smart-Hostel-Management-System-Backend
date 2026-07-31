package com.smart.HostalManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {

    private Long id;
    private String itemType;
    private Integer totalQuantity;
    private Integer workingQuantity;
    private Integer damagedQuantity;
    private String notes;
    private Long roomId;
    private String roomNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
