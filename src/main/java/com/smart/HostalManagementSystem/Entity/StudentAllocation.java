package com.smart.HostalManagementSystem.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "student_allocations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAllocation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;



    @Column(nullable = false)
    private LocalDate allocatedDate;

    @Column(nullable = false)
    private LocalDate releasedDate;



    @Column(nullable = false)
    private String status;   // ACTIVE / INACTIVE

}