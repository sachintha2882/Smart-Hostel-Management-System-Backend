package com.smart.HostalManagementSystem.Config;

import com.smart.HostalManagementSystem.Service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth
                        // ===== NEW: preflight OPTIONS requests okkoma permit karanawa =====
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "STUDENT_AFFAIRS")

                        .requestMatchers(HttpMethod.GET, "/api/canteen-meals/**").hasAnyRole("STUDENT", "CANTEEN", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/canteen-meals/**").hasAnyRole("CANTEEN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/canteen-meals/**").hasAnyRole("CANTEEN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/canteen-meals/**").hasAnyRole("CANTEEN", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/complaints/my-complaints").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/complaints/subwarden").hasAnyRole("SUBWARDEN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/complaints/**").hasAnyRole("ADMIN", "STUDENT", "STUDENT_AFFAIRS", "SUBWARDEN", "MAINTENANCE")
                        .requestMatchers(HttpMethod.POST, "/api/complaints").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/complaints/*/forward", "/api/complaints/*/decline").hasAnyRole("SUBWARDEN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/complaints/*/start", "/api/complaints/*/complete", "/api/complaints/*/resolve").hasAnyRole("MAINTENANCE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/complaints/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/announcements/**").hasAnyRole("STUDENT", "STUDENT_AFFAIRS", "SUBWARDEN", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/announcements").hasAnyRole("STUDENT_AFFAIRS", "SUBWARDEN")
                        .requestMatchers(HttpMethod.DELETE, "/api/announcements/**").hasAnyRole("STUDENT_AFFAIRS", "SUBWARDEN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/announcements/**").hasAnyRole("ADMIN","STUDENT_AFFAIRS", "SUBWARDEN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
