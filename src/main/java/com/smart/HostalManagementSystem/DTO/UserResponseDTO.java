package com.smart.HostalManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String username;
    private String role;
    private boolean enabled;
    private boolean firstLogin;
    private String studentName;   // student role ekakata nam pamanak

}