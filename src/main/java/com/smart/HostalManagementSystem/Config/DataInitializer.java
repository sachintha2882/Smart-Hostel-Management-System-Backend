package com.smart.HostalManagementSystem.Config;

import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Enums.Role;
import com.smart.HostalManagementSystem.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Value("${system.maintenance.username:maintenance1}")
    private String maintenanceUsername;

    @Value("${system.maintenance.password:Maintenance@1234}")
    private String maintenancePassword;

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            User maintenanceUser = userRepository.findByUsernameIgnoreCase(maintenanceUsername).orElse(null);

            if (maintenanceUser == null) {
                maintenanceUser = new User();
                maintenanceUser.setUsername(maintenanceUsername);
                maintenanceUser.setPassword(passwordEncoder.encode(maintenancePassword));
                maintenanceUser.setRole(Role.MAINTENANCE);
                maintenanceUser.setEnabled(true);
                maintenanceUser.setFirstLogin(false);
                maintenanceUser.setForcePasswordChange(false);
                userRepository.save(maintenanceUser);
                System.out.println("Created default maintenance user: " + maintenanceUsername);
            }
        };
    }
}
