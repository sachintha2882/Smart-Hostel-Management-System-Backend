package com.smart.HostalManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintRequestDTO {

    private String title;
    private String description;
    private String category;
    private Long studentId;
    private Long roomId;
}
