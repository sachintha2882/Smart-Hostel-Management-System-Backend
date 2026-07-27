package com.smart.HostalManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCredentialDTO {

    private String registrationNumber;
    private String studentName;
    private String roomNumber;
    private String username;
    private String tempPassword;   // plain text - response ekedi witharak pennanawa, DB ekedi hash wela thiyenne
}