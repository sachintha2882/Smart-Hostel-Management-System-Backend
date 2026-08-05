package com.smart.HostalManagementSystem.Repository;

import com.smart.HostalManagementSystem.Entity.CanteenMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CanteenMealRepository extends JpaRepository<CanteenMeal, Long> {
    List<CanteenMeal> findAllByExpiresAtAfterOrderByUpdatedAtDesc(LocalDateTime now);
    void deleteByMealTypeAndExpiresAtAfter(String mealType, LocalDateTime now);
    long deleteByExpiresAtBefore(LocalDateTime now);
}
