package com.smart.HostalManagementSystem.Repository;

import com.smart.HostalManagementSystem.Entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByTargetType(Announcement.TargetType targetType);

    List<Announcement> findByHostelId(Long hostelId);

}