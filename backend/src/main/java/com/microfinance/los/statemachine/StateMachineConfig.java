package com.microfinance.los.statemachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<LoanState, LoanEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<LoanState, LoanEvent> states) throws Exception {
        states
            .withStates()
            .initial(LoanState.DRAFT)
            .states(EnumSet.allOf(LoanState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<LoanState, LoanEvent> transitions) throws Exception {
        transitions
            .withExternal().source(LoanState.DRAFT).target(LoanState.SUBMITTED).event(LoanEvent.SUBMIT)
            .and()
            .withExternal().source(LoanState.SUBMITTED).target(LoanState.UNDER_REVIEW).event(LoanEvent.RECEIVE_ML_SCORE)
            .and()
            .withExternal().source(LoanState.UNDER_REVIEW).target(LoanState.APPROVED).event(LoanEvent.APPROVE)
            .and()
            .withExternal().source(LoanState.UNDER_REVIEW).target(LoanState.REJECTED).event(LoanEvent.REJECT)
            .and()
            .withExternal().source(LoanState.APPROVED).target(LoanState.DISBURSED).event(LoanEvent.DISBURSE);
    }
}
