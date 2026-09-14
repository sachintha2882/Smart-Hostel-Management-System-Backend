package com.smart.HostalManagementSystem.Controller;

import com.smart.HostalManagementSystem.DTO.RegisterRequestDTO;
import com.smart.HostalManagementSystem.DTO.UserResponseDTO;
import com.smart.HostalManagementSystem.Entity.Hostel;
import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Repository.HostelRepository;
import com.smart.HostalManagementSystem.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HostelRepository hostelRepository;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, HostelRepository hostelRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.hostelRepository = hostelRepository;
    }

    // Get all users (admin panel eke list eka)
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get user by ID
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    // Create user (admin eken manual widiyata user create karanawa)
    @PostMapping
    public UserResponseDTO createUser(@RequestBody RegisterRequestDTO request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();

        if (username.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
        }
        if (request.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        }

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true);
        user.setFirstLogin(true);
        user.setForcePasswordChange(true);

        if (request.getHostelId() != null) {
            Hostel hostel = hostelRepository.findById(request.getHostelId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hostel not found"));
            user.setHostel(hostel);
        }

        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    // Enable/Disable user account
    @PutMapping("/{id}/toggle-status")
    public UserResponseDTO toggleUserStatus(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(!user.isEnabled());
        User updated = userRepository.save(user);
        return convertToDTO(updated);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User deleted successfully";
    }

    // Entity -> DTO convert karana helper
    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole().name());
        dto.setEnabled(user.isEnabled());
        dto.setFirstLogin(user.isFirstLogin());
        dto.setStudentName(user.getStudent() != null ? user.getStudent().getFullName() : null);
        if (user.getHostel() != null) {
            dto.setHostelId(user.getHostel().getId());
            dto.setHostelName(user.getHostel().getHostelName());
        }
        return dto;
    }
}