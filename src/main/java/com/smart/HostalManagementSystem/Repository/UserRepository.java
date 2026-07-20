package com.smart.HostalManagementSystem.Repository;

import com.smart.HostalManagementSystem.Entity.Student;
import com.smart.HostalManagementSystem.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByStudent(Student student);

    boolean existsByStudent(Student student);

    boolean existsByUsername(String username);

}