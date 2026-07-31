package com.smart.HostalManagementSystem.Controller;

import com.smart.HostalManagementSystem.DTO.InventoryRequestDTO;
import com.smart.HostalManagementSystem.DTO.InventoryResponseDTO;
import com.smart.HostalManagementSystem.Service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    // Create Inventory Item
    @PostMapping
    public InventoryResponseDTO createInventory(
            @RequestBody InventoryRequestDTO dto) {
        return inventoryService.createInventory(dto);
    }


    // Get All Inventory Items
    @GetMapping
    public List<InventoryResponseDTO> getAllInventory() {
        return inventoryService.getAllInventory();
    }


    // Get Inventory Item By ID
    @GetMapping("/{id}")
    public InventoryResponseDTO getInventoryById(
            @PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }


    // Get Inventory By Room
    @GetMapping("/room/{roomId}")
    public List<InventoryResponseDTO> getInventoryByRoom(
            @PathVariable Long roomId) {
        return inventoryService.getInventoryByRoom(roomId);
    }


    // Get Inventory By Item Type
    @GetMapping("/type/{itemType}")
    public List<InventoryResponseDTO> getInventoryByType(
            @PathVariable String itemType) {
        return inventoryService.getInventoryByType(itemType);
    }


    // Update Inventory Item
    @PutMapping("/{id}")
    public InventoryResponseDTO updateInventory(
            @PathVariable Long id,
            @RequestBody InventoryRequestDTO dto) {
        return inventoryService.updateInventory(id, dto);
    }


    // Delete Inventory Item
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInventory(
            @PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok("Inventory item deleted successfully");
    }
}
