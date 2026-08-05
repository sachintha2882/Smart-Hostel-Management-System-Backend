package com.smart.HostalManagementSystem.DTO;

import lombok.Data;

@Data
public class CanteenMealRequestDTO {
    private String mealType;

    private String mainDishes;
    private String curries;
    private String shortEats;
}
