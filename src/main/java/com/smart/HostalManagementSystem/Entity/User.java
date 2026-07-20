package com.smart.HostalManagementSystem.Entity;


import com.smart.HostalManagementSystem.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Login Username
    @Column(nullable = false, unique = true)
    private String username;

    // BCrypt Password
    @Column(nullable = false)
    private String password;

    // User Role
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Account Active / Disabled
    @Column(nullable = false)
    private boolean enabled = true;

    // First Login Check
    @Column(nullable = false)
    private boolean firstLogin = true;

    // Link with Student
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", unique = true)
    private Student student;

}