package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.DTO.HostelRequestDTO;
import com.smart.HostalManagementSystem.DTO.HostelResponseDTO;
import com.smart.HostalManagementSystem.Entity.Hostel;
import com.smart.HostalManagementSystem.Repository.HostelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HostelService {

    private final HostelRepository hostelRepository;

    public HostelService(HostelRepository hostelRepository) {
        this.hostelRepository = hostelRepository;
    }

    // Create Hostel
    public HostelResponseDTO createHostel(HostelRequestDTO dto) {

        Hostel hostel = new Hostel();

        hostel.setHostelName(dto.getHostelName());
        hostel.setHostelType(dto.getHostelType());
        hostel.setLocation(dto.getLocation());

        // Hostel capacity is SYSTEM-CONTROLLED: derived from its Buildings.
        // A new Hostel has no Buildings yet, so capacity is 0.
        hostel.setTotalCapacity(0);

        Hostel savedHostel = hostelRepository.save(hostel);

        return mapToResponse(savedHostel);
    }

    // Get All Hostels
    public List<HostelResponseDTO> getAllHostels() {

        return hostelRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get Hostel By Id
    public HostelResponseDTO getHostelById(Long id) {

        Hostel hostel = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        return mapToResponse(hostel);
    }

    // Update Hostel
    @Transactional
    public HostelResponseDTO updateHostel(Long id, HostelRequestDTO dto) {

        Hostel hostel = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        hostel.setHostelName(dto.getHostelName());
        hostel.setHostelType(dto.getHostelType());
        hostel.setLocation(dto.getLocation());

        // Hostel capacity is SYSTEM-CONTROLLED: never set from the DTO.
        // Recompute it from the current Buildings so it stays accurate.
        recalculateTotalCapacity(id);

        Hostel updatedHostel = hostelRepository.save(hostel);

        return mapToResponse(updatedHostel);
    }

    // Delete Hostel
    public void deleteHostel(Long id) {

        Hostel hostel = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        hostelRepository.delete(hostel);
    }

    // SYSTEM-CONTROLLED CAPACITY
    // Hostel totalCapacity = sum of room capacities across all its Buildings
    // (Building -> Floors -> Rooms).
    @Transactional
    public void recalculateTotalCapacity(Long hostelId) {

        Hostel hostel = hostelRepository.findById(hostelId)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        int totalCapacity = hostel.getBuildings().stream()
                .flatMap(building -> building.getFloors().stream())
                .flatMap(floor -> floor.getRooms().stream())
                .mapToInt(room -> room.getCapacity() != null ? room.getCapacity() : 0)
                .sum();

        hostel.setTotalCapacity(totalCapacity);

        hostelRepository.save(hostel);
    }

    // Convert Entity -> Response DTO
    private HostelResponseDTO mapToResponse(Hostel hostel) {

        HostelResponseDTO response = new HostelResponseDTO();

        response.setId(hostel.getId());
        response.setHostelName(hostel.getHostelName());
        response.setHostelType(hostel.getHostelType());
        response.setLocation(hostel.getLocation());
        response.setTotalCapacity(hostel.getTotalCapacity());
        response.setStatus(hostel.getStatus());

        return response;
    }
}
