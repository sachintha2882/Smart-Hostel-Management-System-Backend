package com.smart.HostalManagementSystem.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {


    private Long id;

    private String registrationNumber;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String nic;

    private String gender;

    private String faculty;

    private String academicYear;

    private String address;

}