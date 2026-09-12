package com.smart.HostalManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for submitting a complaint.
 * studentId and roomId are intentionally NOT included here —
 * they are derived server-side from the authenticated user's
 * active StudentAllocation record. This prevents a student from
 * submitting a complaint for another student's room.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintRequestDTO {

    private String title;
    private String description;
    private String category;
}
