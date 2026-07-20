package com.smart.HostalManagementSystem.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {


    private String token;

    private String username;

    private String role;


}