package com.smart.HostalManagementSystem.Controller;


import com.smart.HostalManagementSystem.DTO.LoginRequestDTO;
import com.smart.HostalManagementSystem.DTO.LoginResponseDTO;
import com.smart.HostalManagementSystem.DTO.RegisterRequestDTO;
import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Repository.UserRepository;
import com.smart.HostalManagementSystem.Service.JwtService;
import com.smart.HostalManagementSystem.Service.UserService;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {


    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService
    ){

        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequestDTO request
    ){
        //for a testing
        System.out.println("REGISTER API HIT");

        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(request.getRole());


        user.setEnabled(true);

        user.setFirstLogin(true);


        userRepository.save(user);


        return ResponseEntity.ok("User Created Successfully");

    }



    @PostMapping("/login")
    public LoginResponseDTO login(
            @RequestBody LoginRequestDTO request
    ){


        // Check username and password
        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getUsername(),

                        request.getPassword()

                )

        );



        // Get user details
        User user =
                userService.getUserByUsername(
                        request.getUsername()
                );



        // Generate JWT token
        String token =
                jwtService.generateToken(
                        user.getUsername()
                );



        return new LoginResponseDTO(

                token,

                user.getUsername(),

                user.getRole().name()

        );


    }


}