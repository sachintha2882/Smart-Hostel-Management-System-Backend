package com.smart.HostalManagementSystem.Config;

import com.smart.HostalManagementSystem.Entity.Hostel;
import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Enums.Role;
import com.smart.HostalManagementSystem.Repository.HostelRepository;
import com.smart.HostalManagementSystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
@RequiredArgsConstructor
@Slf4j
public class CanteenAssignmentInitializer implements CommandLineRunner {

    private static final String CANTEEN_USERNAME = "Canteen01";
    private static final String SUBWARDEN_USERNAME = "SubWarden01";
    private static final String HOSTEL_NAME = "Eliyakanda New Boy's Hostel";

    private final UserRepository userRepository;
    private final HostelRepository hostelRepository;

    @Override
    public void run(String... args) {
        User canteen = userRepository.findByUsernameIgnoreCase(CANTEEN_USERNAME).orElse(null);
        if (canteen == null) {
            log.warn("Cannot assign {} because the user does not exist", CANTEEN_USERNAME);
            return;
        }
        if (canteen.getRole() != Role.CANTEEN) {
            log.warn("Cannot assign {} because it is not a CANTEEN account", CANTEEN_USERNAME);
            return;
        }

        Hostel hostel = hostelRepository.findByHostelName(HOSTEL_NAME).orElse(null);
        if (hostel == null) {
            log.warn("Cannot assign {} because hostel '{}' does not exist", CANTEEN_USERNAME, HOSTEL_NAME);
            return;
        }

        if (canteen.getHostel() == null || !hostel.getId().equals(canteen.getHostel().getId())) {
            canteen.setHostel(hostel);
            userRepository.save(canteen);
            log.info("Assigned {} to {}", CANTEEN_USERNAME, HOSTEL_NAME);
        } else {
            log.info("{} is already assigned to {}", CANTEEN_USERNAME, HOSTEL_NAME);
        }

        User subWarden = userRepository.findByUsernameIgnoreCase(SUBWARDEN_USERNAME).orElse(null);
        if (subWarden == null || subWarden.getRole() != Role.SUBWARDEN) {
            log.warn("Cannot assign {} because the SUBWARDEN account does not exist", SUBWARDEN_USERNAME);
            return;
        }
        if (subWarden.getHostel() == null || !hostel.getId().equals(subWarden.getHostel().getId())) {
            subWarden.setUsername(SUBWARDEN_USERNAME);
            subWarden.setHostel(hostel);
            userRepository.save(subWarden);
            log.info("Assigned {} to {}", SUBWARDEN_USERNAME, HOSTEL_NAME);
        } else {
            log.info("{} is already assigned to {}", SUBWARDEN_USERNAME, HOSTEL_NAME);
        }
    }
}
