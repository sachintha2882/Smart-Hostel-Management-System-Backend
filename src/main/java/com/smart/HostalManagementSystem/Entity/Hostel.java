package com.smart.HostalManagementSystem.Entity;


import com.smart.HostalManagementSystem.Entity.BaseEntity;
import com.smart.HostalManagementSystem.Enums.HostelType;
import com.smart.HostalManagementSystem.Enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "hostels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hostel extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String hostelName;


    @Enumerated(EnumType.STRING)
    private HostelType hostelType;


    private String location;


    private Integer totalCapacity;


    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @OneToMany(
            mappedBy = "hostel",
            cascade =  CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Building> buildings = new ArrayList<>();


}