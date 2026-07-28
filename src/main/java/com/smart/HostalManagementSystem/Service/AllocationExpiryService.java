package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.Entity.Room;
import com.smart.HostalManagementSystem.Entity.StudentAllocation;
import com.smart.HostalManagementSystem.Repository.RoomRepository;
import com.smart.HostalManagementSystem.Repository.StudentAllocationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AllocationExpiryService {

    private final StudentAllocationRepository allocationRepository;
    private final RoomRepository roomRepository;

    public AllocationExpiryService(
            StudentAllocationRepository allocationRepository,
            RoomRepository roomRepository) {
        this.allocationRepository = allocationRepository;
        this.roomRepository = roomRepository;
    }

    // Hemaviyawema midnight 00:00 ta automatically run wenawa
    @Scheduled(cron = "0 0 0 * * *")
    public void releaseExpiredAllocations() {

        List<StudentAllocation> activeAllocations =
                allocationRepository.findByStatus("ACTIVE");

        LocalDate today = LocalDate.now();
        int releasedCount = 0;

        for (StudentAllocation allocation : activeAllocations) {

            LocalDate expiryDate = allocation.getExpectedReleaseDate();

            // expectedReleaseDate eka set wela thiyena, e date eka pass una allocations witharak release karanawa
            if (expiryDate != null && !today.isBefore(expiryDate)) {

                allocation.setStatus("INACTIVE");
                allocation.setReleasedDate(today);
                allocationRepository.save(allocation);

                // Room occupancy eka -1 karanawa
                Room room = allocation.getRoom();
                if (room.getCurrentOccupancy() > 0) {
                    room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
                    roomRepository.save(room);
                }

                releasedCount++;
            }
        }

        System.out.println("Auto-released " + releasedCount + " expired allocations.");
    }
}