package com.smart.HostalManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "canteen_meals")
@Getter
@Setter
@NoArgsConstructor
public class CanteenMeal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String mealType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String menuItems;

    @Column(columnDefinition = "TEXT")
    private String mainDishes;

    @Column(columnDefinition = "TEXT")
    private String curries;

    @Column(columnDefinition = "TEXT")
    private String shortEats;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
