package com.smart.HostalManagementSystem.Controller;

import com.smart.HostalManagementSystem.DTO.CanteenMealRequestDTO;
import com.smart.HostalManagementSystem.DTO.CanteenMealResponseDTO;
import com.smart.HostalManagementSystem.DTO.CanteenHostelResponseDTO;
import com.smart.HostalManagementSystem.Entity.StudentAllocation;
import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Repository.StudentAllocationRepository;
import com.smart.HostalManagementSystem.Repository.UserRepository;
import com.smart.HostalManagementSystem.Service.CanteenMealService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/canteen-meals")
public class CanteenMealController {
    private final CanteenMealService service;
    private final UserRepository userRepository;
    private final StudentAllocationRepository allocationRepository;

    public CanteenMealController(CanteenMealService service,
                                UserRepository userRepository,
                                StudentAllocationRepository allocationRepository) {
        this.service = service;
        this.userRepository = userRepository;
        this.allocationRepository = allocationRepository;
    }

    @GetMapping
    public List<CanteenMealResponseDTO> getActiveMeals(
            @RequestParam(required = false) Long hostelId,
            Authentication authentication) {
        if (hasRole(authentication, "STUDENT") || hasRole(authentication, "CANTEEN")) {
            return getMyHostelMeals(authentication);
        }
        if (hostelId != null) {
            return service.getActiveMealsByHostel(hostelId);
        }
        return service.getActiveMeals();
    }

    @GetMapping("/my-hostel")
    public List<CanteenMealResponseDTO> getMyHostelMeals(Authentication authentication) {
        if (authentication == null) {
            return List.of();
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return List.of();
        }

        // If user is a staff member with an assigned hostel
        if (user.getHostel() != null) {
            return service.getActiveMealsByHostel(user.getHostel().getId());
        }

        // If user is a student with an active room allocation
        if (user.getStudent() != null) {
            List<StudentAllocation> allocations = allocationRepository.findByStudentId(user.getStudent().getId());
            StudentAllocation active = allocations.stream()
                    .filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus()))
                    .findFirst()
                    .orElse(null);
            if (active != null && active.getRoom() != null) {
                Long hostelId = active.getRoom().getFloor().getBuilding().getHostel().getId();
                return service.getActiveMealsByHostel(hostelId);
            }
            // Student is unallocated: return empty list
            return List.of();
        }

        return List.of();
    }

    @GetMapping("/my-assignment")
    public CanteenHostelResponseDTO getMyAssignment(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Canteen account not found"));
        if (user.getHostel() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Canteen staff member is not assigned to a hostel"
            );
        }

        return new CanteenHostelResponseDTO(user.getHostel().getId(), user.getHostel().getHostelName());
    }

    @PostMapping
    public CanteenMealResponseDTO create(@RequestBody CanteenMealRequestDTO request, Authentication authentication) {
        // Canteen staff may publish only for their assigned hostel.
        if (hasRole(authentication, "CANTEEN")) {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user == null || user.getHostel() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Canteen staff member is not assigned to a hostel"
                );
            }
            request.setHostelId(user.getHostel().getId());
        }
        return service.create(request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationError(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> ("ROLE_" + role).equals(authority.getAuthority()));
    }

    @PutMapping("/{id}")
    public CanteenMealResponseDTO update(@PathVariable Long id, @RequestBody CanteenMealRequestDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
