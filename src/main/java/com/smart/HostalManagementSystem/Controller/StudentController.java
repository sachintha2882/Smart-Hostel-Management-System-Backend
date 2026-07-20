package com.smart.HostalManagementSystem.Controller;


import com.smart.HostalManagementSystem.DTO.StudentRequestDTO;
import com.smart.HostalManagementSystem.DTO.StudentResponseDTO;
import com.smart.HostalManagementSystem.Service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/students")
@CrossOrigin
public class StudentController {


    private final StudentService studentService;


    public StudentController(StudentService studentService) {

        this.studentService = studentService;

    }



    // Create Student
    @PostMapping
    public StudentResponseDTO createStudent(
            @RequestBody StudentRequestDTO dto) {

        return studentService.createStudent(dto);

    }



    // Get All Students
    @GetMapping
    public List<StudentResponseDTO> getAllStudents() {

        return studentService.getAllStudents();

    }



    // Get Student By ID
    @GetMapping("/{id}")
    public StudentResponseDTO getStudentById(
            @PathVariable Long id) {

        return studentService.getStudentById(id);

    }



    // Update Student
    @PutMapping("/{id}")
    public StudentResponseDTO updateStudent(
            @PathVariable Long id,
            @RequestBody StudentRequestDTO dto) {

        return studentService.updateStudent(id, dto);

    }



    // Delete Student
    @DeleteMapping("/{id}")
    public String deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return "Student deleted successfully";

    }

}