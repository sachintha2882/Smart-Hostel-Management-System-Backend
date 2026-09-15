package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.DTO.AnnouncementRequestDTO;
import com.smart.HostalManagementSystem.DTO.AnnouncementResponseDTO;
import com.smart.HostalManagementSystem.Entity.Announcement;
import com.smart.HostalManagementSystem.Entity.AnnouncementRead;
import com.smart.HostalManagementSystem.Entity.Hostel;
import com.smart.HostalManagementSystem.Repository.AnnouncementReadRepository;
import com.smart.HostalManagementSystem.Repository.AnnouncementRepository;
import com.smart.HostalManagementSystem.Repository.HostelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementReadRepository announcementReadRepository;
    private final HostelRepository hostelRepository;


    // Create Announcement
    public AnnouncementResponseDTO createAnnouncement(
            AnnouncementRequestDTO request,
            String username,
            String role
    ) {

        Announcement announcement = new Announcement();

        announcement.setTitle(request.getTitle());
        announcement.setMessage(request.getMessage());
        announcement.setTargetType(request.getTargetType());
        if (request.getCategory() != null) {
            announcement.setCategory(request.getCategory());
        }
        if (request.getPriority() != null) {
            announcement.setPriority(request.getPriority());
        }
        announcement.setCreatedBy(username);
        announcement.setCreatedByRole(role);

        // If announcement is for a specific hostel
        if (request.getTargetType() == Announcement.TargetType.HOSTEL) {

            if (request.getHostelId() == null) {
                throw new RuntimeException("Hostel ID is required");
            }

            Hostel hostel = hostelRepository.findById(request.getHostelId())
                    .orElseThrow(() -> new RuntimeException("Hostel not found"));

            announcement.setHostel(hostel);
        }

        // ALL announcement should not have a hostel
        if (request.getTargetType() == Announcement.TargetType.ALL) {
            announcement.setHostel(null);
        }

        Announcement savedAnnouncement =
                announcementRepository.save(announcement);

        return convertToResponse(savedAnnouncement);
    }

    // Update Announcement
    public AnnouncementResponseDTO updateAnnouncement(
            Long id,
            AnnouncementRequestDTO request,
            String username,
            String role
    ) {

        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        checkOwnershipOrAdmin(announcement, username, role);

        announcement.setTitle(request.getTitle());
        announcement.setMessage(request.getMessage());
        announcement.setTargetType(request.getTargetType());
        if (request.getCategory() != null) {
            announcement.setCategory(request.getCategory());
        }
        if (request.getPriority() != null) {
            announcement.setPriority(request.getPriority());
        }
        if (request.getTargetType() == Announcement.TargetType.HOSTEL) {

            if (request.getHostelId() == null) {
                throw new RuntimeException("Hostel ID is required");
            }

            Hostel hostel = hostelRepository.findById(request.getHostelId())
                    .orElseThrow(() -> new RuntimeException("Hostel not found"));

            announcement.setHostel(hostel);
        }

        // If announcement is for everyone
        if (request.getTargetType() == Announcement.TargetType.ALL) {
            announcement.setHostel(null);
        }

        Announcement updatedAnnouncement =
                announcementRepository.save(announcement);

        return convertToResponse(updatedAnnouncement);
    }

    public void deleteAnnouncement(Long id, String username, String role) {

        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        checkOwnershipOrAdmin(announcement, username, role);

        announcementRepository.deleteById(id);
    }


    // Ownership check: STUDENT_AFFAIRS and ADMIN can edit/delete any;
    // SUB_WARDEN can only edit/delete their own.
    private void checkOwnershipOrAdmin(
            Announcement announcement,
            String username,
            String role
    ) {
        if (role.contains("STUDENT_AFFAIRS") || role.contains("ADMIN")) {
            return;
        }
        if (!username.equals(announcement.getCreatedBy())) {
            throw new RuntimeException("You can only edit your own announcements");
        }
    }


    // Get all announcements (with per-user read state)
    public List<AnnouncementResponseDTO> getAllAnnouncements(String username) {

        Set<Long> readIds = getReadAnnouncementIds(username);

        return announcementRepository.findAll()
                .stream()
                .map(announcement ->
                        convertToResponse(announcement, readIds)
                )
                .toList();
    }


    // Get announcements for a specific hostel (with per-user read state)
    public List<AnnouncementResponseDTO> getHostelAnnouncements(Long hostelId, String username) {

        Set<Long> readIds = getReadAnnouncementIds(username);

        return announcementRepository.findByHostelId(hostelId)
                .stream()
                .map(announcement ->
                        convertToResponse(announcement, readIds)
                )
                .toList();
    }


    // Mark an announcement as read for a user (idempotent)
    public void markAsRead(Long announcementId, String username) {

        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        boolean alreadyRead = announcementReadRepository
                .findByAnnouncementIdAndUsername(announcementId, username)
                .isPresent();

        if (!alreadyRead) {

            AnnouncementRead announcementRead = new AnnouncementRead();
            announcementRead.setAnnouncement(announcement);
            announcementRead.setUsername(username);

            announcementReadRepository.save(announcementRead);
        }
    }


    // Mark an announcement as unread for a user (idempotent)
    public void markAsUnread(Long announcementId, String username) {

        announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        announcementReadRepository
                .findByAnnouncementIdAndUsername(announcementId, username)
                .ifPresent(announcementReadRepository::delete);
    }


    // Announcement ids already read by the user
    private Set<Long> getReadAnnouncementIds(String username) {

        return announcementReadRepository.findByUsername(username)
                .stream()
                .map(announcementRead ->
                        announcementRead.getAnnouncement().getId()
                )
                .collect(Collectors.toSet());
    }


    // Convert Entity -> Response
    private AnnouncementResponseDTO convertToResponse(
            Announcement announcement
    ) {

        return convertToResponse(announcement, Set.of());
    }

    private AnnouncementResponseDTO convertToResponse(
            Announcement announcement,
            Set<Long> readIds
    ) {

        AnnouncementResponseDTO response =
                new AnnouncementResponseDTO();

        response.setId(announcement.getId());
        response.setTitle(announcement.getTitle());
        response.setMessage(announcement.getMessage());
        response.setTargetType(announcement.getTargetType());
        response.setCreatedBy(announcement.getCreatedBy());
        response.setCreatedByRole(announcement.getCreatedByRole());
        response.setCreatedAt(announcement.getCreatedAt());
        response.setCategory(announcement.getCategory());
        response.setPriority(announcement.getPriority());
        response.setRead(readIds.contains(announcement.getId()));

        if (announcement.getHostel() != null) {

            response.setHostelId(
                    announcement.getHostel().getId()
            );

            response.setHostelName(
                    announcement.getHostel().getHostelName()
            );
        }

        return response;
    }
}