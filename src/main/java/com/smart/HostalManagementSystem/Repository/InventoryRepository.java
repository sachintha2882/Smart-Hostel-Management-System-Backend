package com.smart.HostalManagementSystem.Repository;

import com.smart.HostalManagementSystem.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByRoomId(Long roomId);

    List<Inventory> findByItemType(String itemType);

    boolean existsByItemTypeAndRoomId(String itemType, Long roomId);
}
