package com.smart.HostalManagementSystem.Controller;

import com.smart.HostalManagementSystem.DTO.CanteenHostelResponseDTO;
import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/staff-assignment")
@RequiredArgsConstructor
public class StaffAssignmentController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public CanteenHostelResponseDTO getMyAssignment(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        User user = userRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff account not found"));
        if (user.getHostel() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff member is not assigned to a hostel");
        }

        return new CanteenHostelResponseDTO(user.getHostel().getId(), user.getHostel().getHostelName());
    }
}
