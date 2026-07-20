package com.smart.HostalManagementSystem.Service;


import com.smart.HostalManagementSystem.Entity.User;
import com.smart.HostalManagementSystem.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;

    }


    // Spring Security uses this method during login
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {


        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );


        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .disabled(!user.isEnabled())
                .build();

    }


    // Find user by username
    public User getUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

    }


    // Check whether username already exists
    public boolean usernameExists(String username) {

        return userRepository.existsByUsername(username);

    }


    // Save user
    public User saveUser(User user) {

        return userRepository.save(user);

    }


}