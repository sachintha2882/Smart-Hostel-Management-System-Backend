package com.smart.HostalManagementSystem.Service;


import com.smart.HostalManagementSystem.DTO.StudentAllocationRequestDTO;
import com.smart.HostalManagementSystem.DTO.StudentAllocationResponseDTO;
import com.smart.HostalManagementSystem.Entity.Room;
import com.smart.HostalManagementSystem.Entity.Student;
import com.smart.HostalManagementSystem.Entity.StudentAllocation;
import com.smart.HostalManagementSystem.Repository.RoomRepository;
import com.smart.HostalManagementSystem.Repository.StudentAllocationRepository;
import com.smart.HostalManagementSystem.Repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class StudentAllocationService {


    private final StudentAllocationRepository allocationRepository;

    private final StudentRepository studentRepository;

    private final RoomRepository roomRepository;



    public StudentAllocationService(
            StudentAllocationRepository allocationRepository,
            StudentRepository studentRepository,
            RoomRepository roomRepository) {


        this.allocationRepository = allocationRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;

    }




    // Create Allocation
    public StudentAllocationResponseDTO createAllocation(
            StudentAllocationRequestDTO dto) {



        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));



        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));



        // Check student already allocated
        if(allocationRepository
                .existsByStudentIdAndStatus(
                        student.getId(),
                        "ACTIVE")) {


            throw new RuntimeException(
                    "Student already has an active room allocation");

        }



        // Check room capacity
        long currentCount =
                allocationRepository.countByRoomIdAndStatus(
                        room.getId(),
                        "ACTIVE");



        if(currentCount >= room.getCapacity()) {

            throw new RuntimeException(
                    "Room capacity is full");

        }



        StudentAllocation allocation = new StudentAllocation();


        allocation.setStudent(student);

        allocation.setRoom(room);

        allocation.setAllocatedDate(LocalDate.now());

        allocation.setStatus("ACTIVE");


        StudentAllocation saved =
                allocationRepository.save(allocation);

        room.setCurrentOccupancy(
                room.getCurrentOccupancy() + 1);

        roomRepository.save(room);

        Room updatedRoom = roomRepository.save(room);

        System.out.println("Database Occupancy : " + updatedRoom.getCurrentOccupancy());

        System.out.println(
                "Room Occupancy After Update: "
                        + room.getCurrentOccupancy()
        );

        return convertToDTO(saved);

    }



    public StudentAllocationResponseDTO releaseAllocation(Long id){

        StudentAllocation allocation =
                allocationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Allocation not found"));


        if(allocation.getStatus().equals("INACTIVE")){

            throw new RuntimeException(
                    "Allocation already inactive");
        }


        // Change allocation status
        allocation.setStatus("INACTIVE");

        allocation.setReleasedDate(
                LocalDate.now()
        );


        // Update room occupancy
        Room room = allocation.getRoom();

        if(room.getCurrentOccupancy() > 0){

            room.setCurrentOccupancy(
                    room.getCurrentOccupancy() - 1
            );

            roomRepository.save(room);
        }


        StudentAllocation updated =
                allocationRepository.save(allocation);


        return convertToDTO(updated);
    }

    // Get All Allocations
    public List<StudentAllocationResponseDTO> getAllAllocations(){


        return allocationRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }





    // Get Allocation By ID
    public StudentAllocationResponseDTO getAllocationById(Long id){


        StudentAllocation allocation =
                allocationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Allocation not found"));



        return convertToDTO(allocation);

    }





    // Get Student Allocations
    public List<StudentAllocationResponseDTO>
    getAllocationsByStudent(Long studentId){


        return allocationRepository
                .findByStudentId(studentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }





    // Get Room Members
    public List<StudentAllocationResponseDTO>
    getRoomMembers(Long roomId){


        return allocationRepository
                .findByRoomId(roomId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }





    // Delete Allocation
    public void deleteAllocation(Long id){

        allocationRepository.deleteById(id);

    }





    private StudentAllocationResponseDTO convertToDTO(
            StudentAllocation allocation){



        StudentAllocationResponseDTO dto =
                new StudentAllocationResponseDTO();



        dto.setId(allocation.getId());


        dto.setStudentId(
                allocation.getStudent().getId());


        dto.setStudentName(
                allocation.getStudent().getFullName());


        dto.setRegistrationNumber(
                allocation.getStudent().getRegistrationNumber());



        dto.setRoomId(
                allocation.getRoom().getId());


        dto.setRoomNumber(
                allocation.getRoom().getRoomNumber());


        dto.setAllocatedDate(
                allocation.getAllocatedDate());

        dto.setReleasedDate(
                allocation.getReleasedDate()
        );


        dto.setStatus(
                allocation.getStatus());


        return dto;

    }

}