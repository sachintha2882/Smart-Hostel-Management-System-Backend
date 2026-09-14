package com.smart.HostalManagementSystem.Repository;


import com.smart.HostalManagementSystem.Entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface HostelRepository extends JpaRepository<Hostel,Long> {

    boolean existsByHostelName(String hostelName);

    Optional<Hostel> findByHostelName(String hostelName);
}