package com.smart.HostalManagementSystem.Entity;


import com.smart.HostalManagementSystem.Entity.BaseEntity;
import com.smart.HostalManagementSystem.Enums.HostelType;
import com.smart.HostalManagementSystem.Enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "hostels")
@Getter
@Setter
@NoArgsConstructor
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


}