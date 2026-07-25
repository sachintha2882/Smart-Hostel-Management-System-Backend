package com.smart.HostalManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String floorName;

    private int floorNumber;

    @ManyToOne
    @JoinColumn(name = "building_id",nullable = false)
    private Building building ;

    @OneToMany(
            mappedBy = "floor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Room> rooms;
}