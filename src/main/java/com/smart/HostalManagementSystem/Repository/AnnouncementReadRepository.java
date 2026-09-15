package com.smart.HostalManagementSystem.Repository;

import com.smart.HostalManagementSystem.Entity.AnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnouncementReadRepository extends JpaRepository<AnnouncementRead, Long> {

    Optional<AnnouncementRead> findByAnnouncementIdAndUsername(Long announcementId, String username);

    List<AnnouncementRead> findByUsername(String username);

}