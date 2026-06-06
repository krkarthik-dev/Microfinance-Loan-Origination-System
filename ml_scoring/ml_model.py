import numpy as np
from sklearn.linear_model import LogisticRegression

# Create a dummy trained model for calculating Probability of Default (PoD)
model = LogisticRegression()

# Dummy data: features could be [loan_amount, debt_to_income_ratio]
# 0 = paid, 1 = default
X_dummy = np.array([
    [1000, 20], 
    [50000, 70], 
    [5000, 30], 
    [20000, 60]
])
y_dummy = np.array([0, 1, 0, 1])
model.fit(X_dummy, y_dummy)

def feature_engineering(loan_data: dict) -> np.ndarray:
    """
    Perform feature engineering on incoming loan data.
    Extracts features and formats them for the model.
    """
    # Extract features, providing default values if missing
    amount = float(loan_data.get('amount', 5000.0))
    dti = float(loan_data.get('dti', 30.0)) # Debt to income ratio
    
    return np.array([[amount, dti]])

def score_loan(loan_data: dict) -> int:
    """
    Calculates Probability of Default (PoD) and converts it to a 300-900 scale.
    """
    features = feature_engineering(loan_data)
    
    # Calculate Probability of Default
    # predict_proba returns [[prob_class_0, prob_class_1]]
    pod = model.predict_proba(features)[0][1] 
    
    # Convert PoD (0.0 to 1.0) to a 300-900 credit score scale
    # If PoD is 0 -> score is 900 (Lowest risk)
    # If PoD is 1 -> score is 300 (Highest risk)
    score = 900 - (pod * 600)
    
    return int(round(score))
