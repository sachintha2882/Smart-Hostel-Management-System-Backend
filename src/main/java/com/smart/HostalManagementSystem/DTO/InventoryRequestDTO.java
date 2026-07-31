package com.smart.HostalManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequestDTO {

    private String itemType;
    private Integer totalQuantity;
    private Integer workingQuantity;
    private Integer damagedQuantity;
    private String notes;
    private Long roomId;
}
