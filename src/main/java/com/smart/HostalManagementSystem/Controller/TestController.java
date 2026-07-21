package com.smart.HostalManagementSystem.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {


    @GetMapping
    public String test(){

        return "JWT Authentication Working";

    }


    @GetMapping("/user")
    public String user(Authentication authentication){

        return "Logged User : " + authentication.getName();

    }

}