package com.smart.HostalManagementSystem.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MaintenanceCompletionRequestDTO {

    @NotBlank(message = "A maintenance completion note is required")
    private String remarks;
}
