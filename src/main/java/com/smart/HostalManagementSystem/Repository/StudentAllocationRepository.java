package com.smart.HostalManagementSystem.Repository;


import com.smart.HostalManagementSystem.Entity.StudentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StudentAllocationRepository extends JpaRepository<StudentAllocation, Long> {


    // Find all allocations of a specific student
    List<StudentAllocation> findByStudentId(Long studentId);



    // Find all students allocated to a room
    List<StudentAllocation> findByRoomId(Long roomId);



    // Check whether a student already has an active allocation
    boolean existsByStudentIdAndStatus(Long studentId, String status);



    // Check room occupancy
    long countByRoomIdAndStatus(Long roomId, String status);

}