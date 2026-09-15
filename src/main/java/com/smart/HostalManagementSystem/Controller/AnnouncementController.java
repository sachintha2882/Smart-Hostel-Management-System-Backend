package com.smart.HostalManagementSystem.Controller;

import com.smart.HostalManagementSystem.DTO.AnnouncementRequestDTO;
import com.smart.HostalManagementSystem.DTO.AnnouncementResponseDTO;
import com.smart.HostalManagementSystem.Service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@CrossOrigin
public class AnnouncementController {

    private final AnnouncementService announcementService;


    // Create announcement
    @PostMapping
    public ResponseEntity<AnnouncementResponseDTO> createAnnouncement(
            @RequestBody AnnouncementRequestDTO request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse("");

        AnnouncementResponseDTO response =
                announcementService.createAnnouncement(
                        request,
                        username,
                        role
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    // Get all announcements (with read state for the current user)
    @GetMapping
    public ResponseEntity<List<AnnouncementResponseDTO>> getAllAnnouncements(
            Authentication authentication
    ) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                announcementService.getAllAnnouncements(username)
        );
    }


    // Get announcements of a hostel (with read state for the current user)
    @GetMapping("/hostel/{hostelId}")
    public ResponseEntity<List<AnnouncementResponseDTO>> getHostelAnnouncements(
            @PathVariable Long hostelId,
            Authentication authentication
    ) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                announcementService.getHostelAnnouncements(hostelId, username)
        );
    }


    // Mark an announcement as read for the current user
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAnnouncementAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String username = authentication.getName();

        announcementService.markAsRead(id, username);

        return ResponseEntity.ok().build();
    }


    // Mark an announcement as unread for the current user
    @PostMapping("/{id}/unread")
    public ResponseEntity<Void> markAnnouncementAsUnread(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String username = authentication.getName();

        announcementService.markAsUnread(id, username);

        return ResponseEntity.ok().build();
    }


    // Update announcement
    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementResponseDTO> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody AnnouncementRequestDTO request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse("");

        AnnouncementResponseDTO response =
                announcementService.updateAnnouncement(
                        id,
                        request,
                        username,
                        role
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String username = authentication.getName();

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse("");

        announcementService.deleteAnnouncement(id, username, role);

        return ResponseEntity.noContent().build();
    }
}