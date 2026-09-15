package com.smart.HostalManagementSystem.Repository;


import com.smart.HostalManagementSystem.Entity.StudentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface StudentAllocationRepository extends JpaRepository<StudentAllocation, Long> {


    // Find all allocations of a specific student
    List<StudentAllocation> findByStudentId(Long studentId);



    // Find all students allocated to a room
    List<StudentAllocation> findByRoomId(Long roomId);


    List<StudentAllocation> findByStatus(String status);

    List<StudentAllocation> findByRoom_Floor_Building_Hostel_Id(Long hostelId);



    // Check whether a student already has an active allocation
    boolean existsByStudentIdAndStatus(Long studentId, String status);



    // Check room occupancy
    long countByRoomIdAndStatus(Long roomId, String status);

    // Bulk delete that bypasses the persistence context (avoids ObjectDeletedException).
    @Modifying
    @Query("DELETE FROM StudentAllocation a WHERE a.room.id IN :roomIds")
    void deleteByRoomIds(@Param("roomIds") Collection<Long> roomIds);

}