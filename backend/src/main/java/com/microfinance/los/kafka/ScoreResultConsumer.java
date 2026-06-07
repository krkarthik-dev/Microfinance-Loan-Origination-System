package com.microfinance.los.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.los.entity.CreditScore;
import com.microfinance.los.entity.Loan;
import com.microfinance.los.repository.LoanRepository;
import com.microfinance.los.statemachine.LoanEvent;
import com.microfinance.los.statemachine.LoanState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScoreResultConsumer {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private StateMachineFactory<LoanState, LoanEvent> stateMachineFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "score_results", groupId = "los_backend_group")
    @Transactional
    public void consumeScoreResult(String message) {
        try {
            Map<String, Object> result = objectMapper.readValue(message, Map.class);
            UUID loanId = UUID.fromString((String) result.get("loan_id"));
            Number scoreNum = (Number) result.get("score");
            Integer score = scoreNum != null ? scoreNum.intValue() : null;
            String riskTier = (String) result.get("risk_tier");
            Number podNum = (Number) result.get("pod");
            java.math.BigDecimal pod = podNum != null ? java.math.BigDecimal.valueOf(podNum.doubleValue()) : java.math.BigDecimal.ZERO;

            Optional<Loan> loanOpt = loanRepository.findById(loanId);
            if (loanOpt.isPresent()) {
                Loan loan = loanOpt.get();
                
                CreditScore creditScore = new CreditScore();
                creditScore.setMlScore(score);
                creditScore.setRiskTier(riskTier);
                creditScore.setPodPercentage(pod);
                creditScore.setLoan(loan);
                
                loan.setCreditScore(creditScore);
                
                // Initialize state machine
                StateMachine<LoanState, LoanEvent> sm = stateMachineFactory.getStateMachine(loanId.toString());
                sm.start();
                
                // You could restore the state from loan.getStatus() here to make it robust,
                // but assuming it's SUBMITTED, we send RECEIVE_ML_SCORE
                sm.sendEvent(LoanEvent.RECEIVE_ML_SCORE);
                
                // Update loan status
                // Wait, typically Spring StateMachine handles this with a StateMachineInterceptor.
                // For simplicity, we just set the status to the target state manually.
                loan.setStatus(LoanState.UNDER_REVIEW.name());
                
                loanRepository.save(loan);
                System.out.println("Updated loan " + loanId + " with score " + score + " and risk " + riskTier);
            }
        } catch (Exception e) {
            System.err.println("Error processing score result: " + e.getMessage());
        }
    }
}
