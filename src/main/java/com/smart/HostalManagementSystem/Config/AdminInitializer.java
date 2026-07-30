package com.smart.HostalManagementSystem.Config;

import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Enums.Role;
import com.smart.HostalManagementSystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${system.admin.username}")
    private String adminUsername;

    @Value("${system.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        if (userRepository.existsByUsername(adminUsername)) {
            log.info("Admin account already exists.");
            return;
        }

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);

        // First login password change
        admin.setFirstLogin(true);
        admin.setForcePasswordChange(true);

        userRepository.save(admin);

        log.info("Initial ADMIN account created successfully.");
    }
}