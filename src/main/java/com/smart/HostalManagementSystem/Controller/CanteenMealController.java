package com.smart.HostalManagementSystem.Controller;

import com.smart.HostalManagementSystem.DTO.CanteenMealRequestDTO;
import com.smart.HostalManagementSystem.DTO.CanteenMealResponseDTO;
import com.smart.HostalManagementSystem.Service.CanteenMealService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/canteen-meals")
public class CanteenMealController {
    private final CanteenMealService service;
    public CanteenMealController(CanteenMealService service) { this.service = service; }

    @GetMapping
    public List<CanteenMealResponseDTO> getActiveMeals() { return service.getActiveMeals(); }

    @PostMapping
    public CanteenMealResponseDTO create(@RequestBody CanteenMealRequestDTO request) { return service.create(request); }

    @PutMapping("/{id}")
    public CanteenMealResponseDTO update(@PathVariable Long id, @RequestBody CanteenMealRequestDTO request) { return service.update(id, request); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
