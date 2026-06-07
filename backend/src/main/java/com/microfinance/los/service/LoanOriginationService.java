package com.microfinance.los.service;

import com.microfinance.los.dto.LoanRequestDTO;
import com.microfinance.los.entity.Loan;
import com.microfinance.los.entity.User;
import com.microfinance.los.repository.LoanRepository;
import com.microfinance.los.statemachine.LoanEvent;
import com.microfinance.los.statemachine.LoanState;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanOriginationService {

    private static final String KAFKA_TOPIC = "loan_created";

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private StateMachineFactory<LoanState, LoanEvent> stateMachineFactory;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Loan submitLoanApplication(LoanRequestDTO requestDTO, User user) {
        // 1. Initialize State Machine
        StateMachine<LoanState, LoanEvent> sm = stateMachineFactory.getStateMachine();
        sm.start();

        // 2. Trigger Transition DRAFT -> SUBMITTED
        sm.sendEvent(LoanEvent.SUBMIT);
        LoanState currentState = sm.getState().getId();

        // 3. Persist to Database
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setPrincipalAmount(requestDTO.getPrincipalAmount());
        loan.setTenureMonths(requestDTO.getTenureMonths());
        loan.setAge(requestDTO.getAge());
        loan.setDependents(requestDTO.getDependents());
        loan.setGeographicalLocation(requestDTO.getGeographicalLocation());
        loan.setDeclaredIncome(requestDTO.getDeclaredIncome());
        loan.setStatus(currentState.name()); // SUBMITTED
        
        Loan savedLoan = loanRepository.save(loan);

        // 4. Publish to Kafka
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("id", savedLoan.getLoanId().toString());
        data.put("principalAmount", requestDTO.getPrincipalAmount());
        data.put("tenureMonths", requestDTO.getTenureMonths());
        data.put("age", requestDTO.getAge());
        data.put("dependents", requestDTO.getDependents());
        data.put("geographicalLocation", requestDTO.getGeographicalLocation());
        data.put("declaredIncome", requestDTO.getDeclaredIncome());

        com.microfinance.los.dto.LoanEventMessage eventMessage = new com.microfinance.los.dto.LoanEventMessage("loan_created", data);
        
        kafkaTemplate.send("loan_events", savedLoan.getLoanId().toString(), eventMessage);

        return savedLoan;
    }
}
