package com.smart.HostalManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcement_reads",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"announcement_id", "username"}
        ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        readAt = LocalDateTime.now();
    }
}