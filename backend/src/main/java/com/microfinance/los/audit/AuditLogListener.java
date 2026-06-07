package com.microfinance.los.audit;

import com.microfinance.los.entity.AuditLog;
import com.microfinance.los.entity.Loan;
import com.microfinance.los.repository.AuditLogRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditLogListener {

    // Because JPA entity listeners are instantiated by JPA, we use ObjectFactory to get Spring beans
    @Autowired
    private ObjectFactory<AuditLogRepository> auditLogRepositoryFactory;

    @PostPersist
    public void postPersist(Object entity) {
        if (entity instanceof Loan) {
            Loan loan = (Loan) entity;
            createLog(loan, "CREATE", null, loan.getStatus());
        }
    }

    // A simple hack to get old state could involve storing @Transient oldState in Loan,
    // but for simplicity we will just log the new state in PostUpdate.
    @PostUpdate
    public void postUpdate(Object entity) {
        if (entity instanceof Loan) {
            Loan loan = (Loan) entity;
            createLog(loan, "UPDATE", null, loan.getStatus());
        }
    }

    private void createLog(Loan loan, String action, String oldState, String newState) {
        AuditLog log = new AuditLog();
        log.setEntityId(loan.getLoanId());
        log.setEntityType("LOAN");
        log.setAction(action);
        log.setOldState(oldState);
        log.setNewState(newState);
        log.setTimestamp(LocalDateTime.now());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            log.setPerformedBy(auth.getName());
        } else {
            log.setPerformedBy("SYSTEM");
        }

        try {
            AuditLogRepository repo = auditLogRepositoryFactory.getObject();
            repo.save(log);
        } catch (Exception e) {
            System.err.println("Failed to save audit log: " + e.getMessage());
        }
    }
}
