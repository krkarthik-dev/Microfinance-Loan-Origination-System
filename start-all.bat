@echo off
echo ==============================================
echo   Starting Microfinance LOS Services...
echo ==============================================

echo [1/4] Starting Infrastructure (Postgres ^& Kafka)...
docker compose up postgres kafka -d
timeout /t 5 /nobreak >nul

echo [2/4] Starting Java Backend...
start "LOS Backend" cmd /k "cd backend && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo [3/4] Starting ML Scoring Engine...
start "LOS ML Scoring" cmd /k "cd ml_scoring && if not exist venv (python -m venv venv) && call venv\Scripts\activate.bat && pip install -r requirements.txt && uvicorn main:app --reload"

echo [4/4] Starting Angular Frontend...
start "LOS Frontend" cmd /k "cd frontend && npm install && npm start"

echo ==============================================
echo   All services are launching!
echo   - Frontend: http://localhost:4200
echo   - Backend API: http://localhost:8080
echo   - ML Scoring Docs: http://localhost:8000/docs
echo ==============================================
pause
