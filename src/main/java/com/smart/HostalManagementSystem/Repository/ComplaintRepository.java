package com.smart.HostalManagementSystem.Repository;

import com.smart.HostalManagementSystem.Entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Collection;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStudentId(Long studentId);

    List<Complaint> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Complaint> findByStatus(String status);

    List<Complaint> findByRoomId(Long roomId);

    List<Complaint> findByCategory(String category);

    List<Complaint> findByStatusIn(Collection<String> statuses);

    List<Complaint> findByRoomFloorBuildingHostelIdAndStatusIn(Long hostelId, Collection<String> statuses);

    List<Complaint> findByRoomFloorBuildingHostelIdAndStatus(Long hostelId, String status);

    List<Complaint> findByRoomFloorBuildingHostelIdOrderByCreatedAtDesc(Long hostelId);

    // Bulk delete that bypasses the persistence context (avoids ObjectDeletedException).
    @Modifying
    @Query("DELETE FROM Complaint c WHERE c.room.id IN :roomIds")
    void deleteByRoomIds(@Param("roomIds") Collection<Long> roomIds);
}
