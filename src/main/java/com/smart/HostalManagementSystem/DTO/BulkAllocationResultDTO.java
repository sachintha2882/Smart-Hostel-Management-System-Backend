package com.smart.HostalManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkAllocationResultDTO {

    private int successCount = 0;
    private int failedCount = 0;

    private List<String> failedReasons = new ArrayList<>();

    // Success una students walata credentials tika (admin ekata pennanna)
    private List<StudentCredentialDTO> createdAccounts = new ArrayList<>();
}