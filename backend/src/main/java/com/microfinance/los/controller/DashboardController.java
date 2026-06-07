package com.microfinance.los.controller;

import com.microfinance.los.entity.Loan;
import com.microfinance.los.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private LoanRepository loanRepository;

    @GetMapping("/pending-loans")
    @PreAuthorize("hasRole('ROLE_LOAN_OFFICER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Loan>> getPendingLoans() {
        // Here we could filter by status = UNDER_REVIEW or SUBMITTED
        // For simplicity, we just return all loans to the dashboard for now.
        // A real app might have different tabs for different statuses.
        List<Loan> loans = loanRepository.findAll();
        return ResponseEntity.ok(loans);
    }
}
