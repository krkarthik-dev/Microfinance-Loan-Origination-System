-- Extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table: users (3NF Compliant)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: loans (3NF Compliant)
-- Depends fully on the primary key (loan_id). user_id is a foreign key to users.
CREATE TABLE loans (
    loan_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    principal_amount DECIMAL(15, 2) NOT NULL,
    tenure_months INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id) 
        REFERENCES users (id)
        ON DELETE RESTRICT
);

-- Table: credit_scores (3NF Compliant)
-- Separate table for credit scores ensures we don't duplicate loan data
-- if we later decide to have history. Depends fully on score_id.
CREATE TABLE credit_scores (
    score_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    loan_id UUID NOT NULL UNIQUE, -- UNIQUE enforces a 1-to-1 relationship for the active score
    ml_score INT NOT NULL,
    risk_tier VARCHAR(50) NOT NULL,
    pod_percentage DECIMAL(5, 2) NOT NULL, -- Probability of Default
    CONSTRAINT fk_loan
        FOREIGN KEY (loan_id)
        REFERENCES loans (loan_id)
        ON DELETE CASCADE
);

-- Indexes for optimal lookup performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_loans_user_id ON loans(user_id);
CREATE INDEX idx_credit_scores_loan_id ON credit_scores(loan_id);
