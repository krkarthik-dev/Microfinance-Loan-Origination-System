package com.microfinance.los.config;

import com.microfinance.los.entity.User;
import com.microfinance.los.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User customer = User.builder()
                        .email("testcustomer@example.com")
                        .passwordHash(passwordEncoder.encode("test"))
                        .role("ROLE_APPLICANT")
                        .build();
                userRepository.save(customer);

                User agent = User.builder()
                        .email("agent@example.com")
                        .passwordHash(passwordEncoder.encode("test"))
                        .role("ROLE_LOAN_OFFICER")
                        .build();
                userRepository.save(agent);

                User admin = User.builder()
                        .email("admin@example.com")
                        .passwordHash(passwordEncoder.encode("test"))
                        .role("ROLE_ADMIN")
                        .build();
                userRepository.save(admin);
                
                System.out.println("Test users seeded successfully.");
            }
        };
    }
}
