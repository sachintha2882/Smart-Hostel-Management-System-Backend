package com.smart.HostalManagementSystem.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CanteenMealResponseDTO {
    private Long id;
    private String mealType;
    private String menuItems;
    private String mainDishes;
    private String curries;
    private String shortEats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
}
