package com.smart.HostalManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String registrationNumber;


    @Column(nullable = false)
    private String fullName;


    @Column(nullable = false, unique = true)
    private String email;


    private String phoneNumber;


    @Column(nullable = false, unique = true)
    private String nic;


    private String gender;


    private String faculty;


    private String academicYear;


    private String address;

}