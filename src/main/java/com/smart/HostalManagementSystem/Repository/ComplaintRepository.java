package com.smart.HostalManagementSystem.Repository;

import com.smart.HostalManagementSystem.Entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStudentId(Long studentId);

    List<Complaint> findByStatus(String status);

    List<Complaint> findByRoomId(Long roomId);

    List<Complaint> findByCategory(String category);
}
