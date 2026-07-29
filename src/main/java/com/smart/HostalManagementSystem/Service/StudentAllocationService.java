package com.smart.HostalManagementSystem.Service;


import com.smart.HostalManagementSystem.DTO.BulkAllocationResultDTO;
import com.smart.HostalManagementSystem.DTO.StudentAllocationRequestDTO;
import com.smart.HostalManagementSystem.DTO.StudentAllocationResponseDTO;
import com.smart.HostalManagementSystem.DTO.StudentCredentialDTO;
import com.smart.HostalManagementSystem.Entity.Room;
import com.smart.HostalManagementSystem.Entity.Student;
import com.smart.HostalManagementSystem.Entity.StudentAllocation;
import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Enums.Role;
import com.smart.HostalManagementSystem.Repository.RoomRepository;
import com.smart.HostalManagementSystem.Repository.StudentAllocationRepository;
import com.smart.HostalManagementSystem.Repository.StudentRepository;
import  com.smart.HostalManagementSystem.Service.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class StudentAllocationService {


    private final StudentAllocationRepository allocationRepository;

    private final StudentRepository studentRepository;

    private final RoomRepository roomRepository;

    private final ExcelParserService excelParserService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;



    public StudentAllocationService(
            StudentAllocationRepository allocationRepository,
            StudentRepository studentRepository,
            RoomRepository roomRepository,
            ExcelParserService excelParserService,
            UserService userService,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.allocationRepository = allocationRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
        this.excelParserService = excelParserService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService =  emailService;
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

        allocation.setAcedemicYear(dto.getAcademicYear());

        allocation.setExpectedReleaseDate(dto.getExpectedReleaseDate());



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

    //STatus eka active da nedda kiyala balana mthode eka
    // Update Allocation Status (ACTIVE <-> INACTIVE)
    public StudentAllocationResponseDTO updateStatus(
            Long id,
            String status
    ) {

        StudentAllocation allocation =
                allocationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Allocation not found"));


        allocation.setStatus(status);


        if(status.equals("INACTIVE")) {

            allocation.setReleasedDate(
                    LocalDate.now()
            );

        } else if(status.equals("ACTIVE")) {

            allocation.setReleasedDate(null);

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

    public BulkAllocationResultDTO bulkAllocateFromExcel(
            MultipartFile file, Long floorId, String academicYear,String expectedReleaseDate) throws Exception {

        BulkAllocationResultDTO result = new BulkAllocationResultDTO();

        LocalDate parsedExpectedReleaseDate = LocalDate.parse(expectedReleaseDate);

        // 1. Excel eken students list eka parse karanawa
        List<Student> parsedStudents = excelParserService.parseStudentExcel(file);

        // 2. Floor ekeම rooms tika ganawa (dan thiyena findByFloorId method eka use karanawa)
        List<Room> floorRooms = roomRepository.findByFloorId(floorId);

        for (Student parsedStudent : parsedStudents) {

            try {
                // 3. Student already DB eke thiyenawada check karanawa (reg number eken)
                Student student = studentRepository
                        .findByRegistrationNumber(parsedStudent.getRegistrationNumber())
                        .orElse(null);

                // Na nam alut student ekක් widiyata save karanawa
                if (student == null) {
                    student = studentRepository.save(parsedStudent);
                }

                // 4. Student mee floor eke already allocate wela innawada check karanawa
                if (allocationRepository.existsByStudentIdAndStatus(student.getId(), "ACTIVE")) {
                    result.setFailedCount(result.getFailedCount() + 1);
                    result.getFailedReasons().add(
                            student.getRegistrationNumber() + " - Already has an active allocation");
                    continue;
                }

                // 5. Available room ekක් floor eke hoyanawa (capacity full nathi ekක්)
                Room availableRoom = floorRooms.stream()
                        .filter(r -> r.getCurrentOccupancy() < r.getCapacity())
                        .findFirst()
                        .orElse(null);

                if (availableRoom == null) {
                    result.setFailedCount(result.getFailedCount() + 1);
                    result.getFailedReasons().add(
                            student.getRegistrationNumber() + " - No available room in this floor");
                    continue;
                }

                // 6. Allocation record eka create karanawa
                StudentAllocation allocation = new StudentAllocation();
                allocation.setStudent(student);
                allocation.setRoom(availableRoom);
                allocation.setAllocatedDate(LocalDate.now());
                allocation.setStatus("ACTIVE");
                allocation.setAcedemicYear(academicYear);
                allocation.setExpectedReleaseDate(parsedExpectedReleaseDate);

                allocationRepository.save(allocation);

                // 7. Room occupancy update karanawa (in-memory list ekath update karanna one,
                //    e nathnam passe students walata puranu occupancy count eka use wenawa)
                availableRoom.setCurrentOccupancy(availableRoom.getCurrentOccupancy() + 1);
                roomRepository.save(availableRoom);

                // 8. User account eka thiyenawada balanawa, na nam create karanawa
                String username;
                String tempPassword = null;

                if (userService.usernameExists(student.getRegistrationNumber().toLowerCase())) {
                    username = student.getRegistrationNumber().toLowerCase();
                } else {
                    username = generateUsername(student);
                    tempPassword = generateTempPassword();

                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(passwordEncoder.encode(tempPassword));
                    user.setRole(Role.STUDENT);
                    user.setStudent(student);
                    user.setForcePasswordChange(true);
                    user.setFirstLogin(true);
                    userService.saveUser(user);

                    try{
                        emailService.sendCredentialsEmail(
                                student.getEmail(),
                                student.getFullName(),
                                username,
                                tempPassword,
                                availableRoom.getRoomNumber()
                        );
                    } catch (Exception emailError){

                        System.out.println("Email sending failed" + student.getEmail());
                    }
                }

                // 9. Result eke success record eka add karanawa
                StudentCredentialDTO credential = new StudentCredentialDTO();
                credential.setRegistrationNumber(student.getRegistrationNumber());
                credential.setStudentName(student.getFullName());
                credential.setRoomNumber(availableRoom.getRoomNumber());
                credential.setUsername(username);
                credential.setTempPassword(tempPassword); // null nam, kalinma account thibba kiyana eka

                result.getCreatedAccounts().add(credential);
                result.setSuccessCount(result.getSuccessCount() + 1);

            } catch (Exception e) {
                result.setFailedCount(result.getFailedCount() + 1);
                result.getFailedReasons().add(
                        parsedStudent.getRegistrationNumber() + " - Error: " + e.getMessage());
            }
        }

        return result;
    }

    // Username generate karanawa (registration number eken, lowercase)
    private String generateUsername(Student student) {
        return student.getRegistrationNumber().toLowerCase();
    }

    // Random temp password ekක් generate karanawa (8 characters)
    private String generateTempPassword() {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
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

        dto.setAcademicYear(
                allocation.getAcedemicYear());

        dto.setExpectedReleaseDate(
                allocation.getExpectedReleaseDate()
        );


        return dto;

    }

}