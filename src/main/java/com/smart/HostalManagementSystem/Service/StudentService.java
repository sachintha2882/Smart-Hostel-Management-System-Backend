package com.smart.HostalManagementSystem.Service;


import com.smart.HostalManagementSystem.DTO.StudentRequestDTO;
import com.smart.HostalManagementSystem.DTO.StudentResponseDTO;
import com.smart.HostalManagementSystem.Entity.Student;
import com.smart.HostalManagementSystem.Repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class StudentService {


    private final StudentRepository studentRepository;


    public StudentService(StudentRepository studentRepository) {

        this.studentRepository = studentRepository;

    }



    // Create Student
    public StudentResponseDTO createStudent(StudentRequestDTO dto) {


        if(studentRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {

            throw new RuntimeException("Registration number already exists");

        }


        if(studentRepository.existsByEmail(dto.getEmail())) {

            throw new RuntimeException("Email already exists");

        }


        if(studentRepository.existsByNic(dto.getNic())) {

            throw new RuntimeException("NIC already exists");

        }



        Student student = new Student();


        student.setRegistrationNumber(dto.getRegistrationNumber());
        student.setFullName(dto.getFullName());
        student.setEmail(dto.getEmail());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setNic(dto.getNic());
        student.setGender(dto.getGender());
        student.setFaculty(dto.getFaculty());
        student.setAcademicYear(dto.getAcademicYear());
        student.setAddress(dto.getAddress());



        Student savedStudent = studentRepository.save(student);


        return convertToResponseDTO(savedStudent);

    }




    // Get All Students
    public List<StudentResponseDTO> getAllStudents() {


        return studentRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

    }




    // Get Student By ID
    public StudentResponseDTO getStudentById(Long id) {


        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));


        return convertToResponseDTO(student);

    }





    // Update Student
    public StudentResponseDTO updateStudent(Long id,
                                            StudentRequestDTO dto) {


        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));



        student.setFullName(dto.getFullName());
        student.setEmail(dto.getEmail());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setGender(dto.getGender());
        student.setFaculty(dto.getFaculty());
        student.setAcademicYear(dto.getAcademicYear());
        student.setAddress(dto.getAddress());


        Student updatedStudent = studentRepository.save(student);


        return convertToResponseDTO(updatedStudent);

    }




    // Delete Student
    public void deleteStudent(Long id) {


        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));


        studentRepository.delete(student);

    }





    private StudentResponseDTO convertToResponseDTO(Student student) {


        StudentResponseDTO dto = new StudentResponseDTO();


        dto.setId(student.getId());

        dto.setRegistrationNumber(student.getRegistrationNumber());

        dto.setFullName(student.getFullName());

        dto.setEmail(student.getEmail());

        dto.setPhoneNumber(student.getPhoneNumber());

        dto.setNic(student.getNic());

        dto.setGender(student.getGender());

        dto.setFaculty(student.getFaculty());

        dto.setAcademicYear(student.getAcademicYear());

        dto.setAddress(student.getAddress());


        return dto;

    }

}