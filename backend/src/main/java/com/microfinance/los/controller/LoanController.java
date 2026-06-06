package com.microfinance.los.controller;

import com.microfinance.los.dto.LoanRequestDTO;
import com.microfinance.los.dto.MessageResponse;
import com.microfinance.los.entity.Loan;
import com.microfinance.los.entity.User;
import com.microfinance.los.repository.UserRepository;
import com.microfinance.los.security.UserDetailsImpl;
import com.microfinance.los.service.LoanOriginationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanOriginationService loanService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/submit")
    @PreAuthorize("hasRole('ROLE_APPLICANT')")
    public ResponseEntity<?> submitLoan(@Valid @RequestBody LoanRequestDTO requestDTO) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new RuntimeException("Error: User not found."));

        try {
            Loan loan = loanService.submitLoanApplication(requestDTO, user);
            return ResponseEntity.ok(new MessageResponse("Loan application submitted successfully with ID: " + loan.getLoanId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error processing loan: " + e.getMessage()));
        }
    }
}
