import numpy as np
from sklearn.linear_model import LogisticRegression

# Create a dummy trained model for calculating Probability of Default (PoD)
model = LogisticRegression()

# Dummy data: features are [age, income, loan_amount]
# 0 = paid, 1 = default
# Older, higher income, lower loan -> paid (0)
# Younger, lower income, higher loan -> default (1)
X_dummy = np.array([
    [45, 100000, 10000],
    [22, 30000, 20000],
    [35, 75000, 15000],
    [28, 40000, 25000]
])
y_dummy = np.array([0, 1, 0, 1])
model.fit(X_dummy, y_dummy)

def feature_engineering(loan_data: dict) -> np.ndarray:
    """
    Perform feature engineering on incoming loan data.
    Extracts age, income, and loan_amount.
    """
    age = float(loan_data.get('age', 30.0))
    income = float(loan_data.get('income', 50000.0))
    loan_amount = float(loan_data.get('loan_amount', 10000.0))
    
    return np.array([[age, income, loan_amount]])

def get_risk_tier(score: int) -> str:
    """
    Determines the risk tier based on the calculated score.
    """
    if score >= 750:
        return "Excellent"
    elif score >= 650:
        return "Good"
    elif score >= 550:
        return "Fair"
    else:
        return "Poor"

def score_loan(loan_data: dict) -> dict:
    """
    Calculates Probability of Default (PoD), converts it to a 300-900 scale,
    and assigns a risk tier.
    """
    features = feature_engineering(loan_data)
    
    # Calculate Probability of Default
    # predict_proba returns [[prob_class_0, prob_class_1]]
    pod = model.predict_proba(features)[0][1] 
    
    # Convert PoD (0.0 to 1.0) to a 300-900 credit score scale
    # If PoD is 0 -> score is 900 (Lowest risk)
    # If PoD is 1 -> score is 300 (Highest risk)
    score = 900 - (pod * 600)
    final_score = int(round(score))
    
    # Clamp score to 300-900 range
    final_score = max(300, min(900, final_score))
    
    risk_tier = get_risk_tier(final_score)
    
    return {
        "score": final_score,
        "risk_tier": risk_tier,
        "pod": float(pod)
    }
