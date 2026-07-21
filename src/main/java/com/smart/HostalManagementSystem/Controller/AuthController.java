package com.smart.HostalManagementSystem.Controller;


import com.smart.HostalManagementSystem.DTO.LoginRequestDTO;
import com.smart.HostalManagementSystem.DTO.LoginResponseDTO;
import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Service.JwtService;
import com.smart.HostalManagementSystem.Service.UserService;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {


    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtService jwtService;



    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService
    ){

        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;

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