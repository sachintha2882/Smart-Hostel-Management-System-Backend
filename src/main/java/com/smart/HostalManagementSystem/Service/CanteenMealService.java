package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.DTO.CanteenMealRequestDTO;
import com.smart.HostalManagementSystem.DTO.CanteenMealResponseDTO;
import com.smart.HostalManagementSystem.Entity.CanteenMeal;
import com.smart.HostalManagementSystem.Repository.CanteenMealRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CanteenMealService {
    private final CanteenMealRepository repository;

    public CanteenMealService(CanteenMealRepository repository) { this.repository = repository; }

    @Transactional
    public CanteenMealResponseDTO create(CanteenMealRequestDTO request) {
        CanteenMeal meal = new CanteenMeal();
        apply(request, meal);
        // There is one current menu per meal type. Publishing again replaces it.
        repository.deleteByMealTypeAndExpiresAtAfter(meal.getMealType(), LocalDateTime.now());
        meal.setExpiresAt(LocalDateTime.now().plusHours(24));
        return toResponse(repository.save(meal));
    }

    public CanteenMealResponseDTO update(Long id, CanteenMealRequestDTO request) {
        CanteenMeal meal = repository.findById(id).orElseThrow(() -> new RuntimeException("Meal entry not found"));
        apply(request, meal);
        // Editing refreshes the menu for another 24 hours.
        meal.setExpiresAt(LocalDateTime.now().plusHours(24));
        return toResponse(repository.save(meal));
    }

    public List<CanteenMealResponseDTO> getActiveMeals() {
        cleanupExpiredMeals();
        return repository.findAllByExpiresAtAfterOrderByUpdatedAtDesc(LocalDateTime.now()).stream().map(this::toResponse).toList();
    }

    public void delete(Long id) { repository.deleteById(id); }

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredMeals() { repository.deleteByExpiresAtBefore(LocalDateTime.now()); }

    private void apply(CanteenMealRequestDTO request, CanteenMeal meal) {
        String mealType = clean(request.getMealType());
        if (mealType == null || !mealType.matches("(?i)BREAKFAST|LUNCH|DINNER")) {
            throw new IllegalArgumentException("Choose Breakfast, Lunch, or Dinner");
        }
        meal.setMealType(mealType.toUpperCase());
        String mainDishes = clean(request.getMainDishes());
        String curries = clean(request.getCurries());
        String shortEats = clean(request.getShortEats());
        if (mainDishes == null && curries == null && shortEats == null) {
            throw new IllegalArgumentException("Add at least one food item");
        }
        meal.setMainDishes(mainDishes);
        meal.setCurries(curries);
        meal.setShortEats(shortEats);
        // Retained for compatibility with existing database rows.
        meal.setMenuItems(String.join(", ", java.util.stream.Stream.of(mainDishes, curries, shortEats)
                .filter(java.util.Objects::nonNull).toList()));
    }

    private CanteenMealResponseDTO toResponse(CanteenMeal meal) {
        CanteenMealResponseDTO dto = new CanteenMealResponseDTO();
        dto.setId(meal.getId()); dto.setMealType(meal.getMealType()); dto.setMenuItems(meal.getMenuItems());
        dto.setMainDishes(meal.getMainDishes()); dto.setCurries(meal.getCurries()); dto.setShortEats(meal.getShortEats());
        dto.setCreatedAt(meal.getCreatedAt()); dto.setUpdatedAt(meal.getUpdatedAt()); dto.setExpiresAt(meal.getExpiresAt());
        return dto;
    }

    private String clean(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
