package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.DTO.HostelRequestDTO;
import com.smart.HostalManagementSystem.DTO.HostelResponseDTO;
import com.smart.HostalManagementSystem.Entity.Hostel;
import com.smart.HostalManagementSystem.Repository.HostelRepository;
import org.springframework.stereotype.Service;

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
        hostel.setTotalCapacity(dto.getTotalCapacity());

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
    public HostelResponseDTO updateHostel(Long id, HostelRequestDTO dto) {

        Hostel hostel = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        hostel.setHostelName(dto.getHostelName());
        hostel.setHostelType(dto.getHostelType());
        hostel.setLocation(dto.getLocation());
        hostel.setTotalCapacity(dto.getTotalCapacity());

        Hostel updatedHostel = hostelRepository.save(hostel);

        return mapToResponse(updatedHostel);
    }

    // Delete Hostel
    public void deleteHostel(Long id) {

        Hostel hostel = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        hostelRepository.delete(hostel);
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