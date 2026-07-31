package com.smart.HostalManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemType;   // BED, FAN, BULB, CHAIR, DESK, CUPBOARD, MATTRESS, BED_BOARD

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer workingQuantity;

    @Column(nullable = false)
    private Integer damagedQuantity;

    @Column
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
