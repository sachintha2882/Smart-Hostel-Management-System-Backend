package com.smart.HostalManagementSystem.DTO;

import com.smart.HostalManagementSystem.Enums.Role;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    private String username;

    private String password;

    private Role role;



}