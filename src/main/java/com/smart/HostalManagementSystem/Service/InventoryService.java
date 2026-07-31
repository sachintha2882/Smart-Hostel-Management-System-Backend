package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.DTO.InventoryRequestDTO;
import com.smart.HostalManagementSystem.DTO.InventoryResponseDTO;
import com.smart.HostalManagementSystem.Entity.Inventory;
import com.smart.HostalManagementSystem.Entity.Room;
import com.smart.HostalManagementSystem.Repository.InventoryRepository;
import com.smart.HostalManagementSystem.Repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            RoomRepository roomRepository) {
        this.inventoryRepository = inventoryRepository;
        this.roomRepository = roomRepository;
    }


    // Create Inventory Item
    public InventoryResponseDTO createInventory(InventoryRequestDTO dto) {

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Inventory inventory = new Inventory();
        inventory.setItemType(dto.getItemType());
        inventory.setTotalQuantity(dto.getTotalQuantity());
        inventory.setWorkingQuantity(dto.getWorkingQuantity());
        inventory.setDamagedQuantity(dto.getDamagedQuantity());
        inventory.setNotes(dto.getNotes());
        inventory.setRoom(room);

        Inventory saved = inventoryRepository.save(inventory);
        return convertToResponseDTO(saved);
    }


    // Get All Inventory Items
    public List<InventoryResponseDTO> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    // Get Inventory Item By ID
    public InventoryResponseDTO getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));
        return convertToResponseDTO(inventory);
    }


    // Get Inventory By Room
    public List<InventoryResponseDTO> getInventoryByRoom(Long roomId) {
        return inventoryRepository.findByRoomId(roomId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    // Get Inventory By Item Type
    public List<InventoryResponseDTO> getInventoryByType(String itemType) {
        return inventoryRepository.findByItemType(itemType)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    // Update Inventory Item
    public InventoryResponseDTO updateInventory(Long id, InventoryRequestDTO dto) {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        inventory.setItemType(dto.getItemType());
        inventory.setTotalQuantity(dto.getTotalQuantity());
        inventory.setWorkingQuantity(dto.getWorkingQuantity());
        inventory.setDamagedQuantity(dto.getDamagedQuantity());
        inventory.setNotes(dto.getNotes());
        inventory.setRoom(room);

        Inventory updated = inventoryRepository.save(inventory);
        return convertToResponseDTO(updated);
    }


    // Delete Inventory Item
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }


    private InventoryResponseDTO convertToResponseDTO(Inventory inventory) {

        InventoryResponseDTO dto = new InventoryResponseDTO();
        dto.setId(inventory.getId());
        dto.setItemType(inventory.getItemType());
        dto.setTotalQuantity(inventory.getTotalQuantity());
        dto.setWorkingQuantity(inventory.getWorkingQuantity());
        dto.setDamagedQuantity(inventory.getDamagedQuantity());
        dto.setNotes(inventory.getNotes());
        dto.setRoomId(inventory.getRoom().getId());
        dto.setRoomNumber(inventory.getRoom().getRoomNumber());
        dto.setCreatedAt(inventory.getCreatedAt());
        dto.setUpdatedAt(inventory.getUpdatedAt());

        return dto;
    }
}
