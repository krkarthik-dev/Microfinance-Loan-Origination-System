package com.microfinance.los.controller;

import com.microfinance.los.entity.ApplicantProfile;
import com.microfinance.los.entity.User;
import com.microfinance.los.repository.ApplicantProfileRepository;
import com.microfinance.los.repository.UserRepository;
import com.microfinance.los.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
public class ApplicantProfileController {

    @Autowired
    private ApplicantProfileRepository applicantProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('ROLE_APPLICANT')")
    public ResponseEntity<?> createProfile(@RequestBody ApplicantProfile profileRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        if (applicantProfileRepository.findByUserId(user.getId()).isPresent()) {
            return ResponseEntity.badRequest().body("Profile already exists for this user.");
        }

        profileRequest.setUser(user);
        applicantProfileRepository.save(profileRequest);
        
        // Also associate with User
        user.setApplicantProfile(profileRequest);
        userRepository.save(user);

        return ResponseEntity.ok("Profile created successfully!");
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ROLE_APPLICANT')")
    public ResponseEntity<?> getProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return applicantProfileRepository.findByUserId(userDetails.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
