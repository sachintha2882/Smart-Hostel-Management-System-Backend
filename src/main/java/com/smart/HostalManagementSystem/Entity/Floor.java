package com.smart.HostalManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "hostel_id")
    private Hostel hostel;
}