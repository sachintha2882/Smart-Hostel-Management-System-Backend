package com.smart.HostalManagementSystem.Controller;

import com.smart.HostalManagementSystem.DTO.HostelRequestDTO;
import com.smart.HostalManagementSystem.DTO.HostelResponseDTO;
import com.smart.HostalManagementSystem.Service.HostelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hostels")
@CrossOrigin
public class HostelController {

    private final HostelService hostelService;

    public HostelController(HostelService hostelService) {
        this.hostelService = hostelService;
    }

    @PostMapping
    public HostelResponseDTO createHostel(
            @RequestBody HostelRequestDTO hostelRequestDTO
    ) {
        return hostelService.createHostel(hostelRequestDTO);
    }

    @GetMapping
    public List<HostelResponseDTO> getAllHostels() {
        return hostelService.getAllHostels();
    }

    @GetMapping("/{id}")
    public HostelResponseDTO getHostelById(
            @PathVariable Long id
    ) {
        return hostelService.getHostelById(id);
    }

    @PutMapping("/{id}")
    public HostelResponseDTO updateHostel(
            @PathVariable Long id,
            @RequestBody HostelRequestDTO hostelRequestDTO
    ) {
        return hostelService.updateHostel(id, hostelRequestDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteHostel(
            @PathVariable Long id
    ) {

        hostelService.deleteHostel(id);
        return "Hostel deleted successfully.";

    }

}