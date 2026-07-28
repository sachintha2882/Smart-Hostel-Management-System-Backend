package com.smart.HostalManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HostalManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(HostalManagementSystemApplication.class, args);
	}

}
