package com.microfinance.los.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "credit_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "score_id", updatable = false, nullable = false)
    private UUID scoreId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "ml_score", nullable = false)
    private Integer mlScore;

    @Column(name = "risk_tier", nullable = false, length = 50)
    private String riskTier;

    @Column(name = "pod_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal podPercentage;
}
