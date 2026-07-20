package com.smart.HostalManagementSystem.Repository;


import com.smart.HostalManagementSystem.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {


    Optional<Student> findByRegistrationNumber(String registrationNumber);


    Optional<Student> findByEmail(String email);


    Optional<Student> findByNic(String nic);


    boolean existsByRegistrationNumber(String registrationNumber);


    boolean existsByEmail(String email);


    boolean existsByNic(String nic);

}