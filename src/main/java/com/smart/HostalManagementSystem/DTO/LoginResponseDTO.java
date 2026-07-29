package com.smart.HostalManagementSystem.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {


    private String token;

    private String username;

    private String role;

    private boolean forcePasswordChange;

    private  String fullName;


}