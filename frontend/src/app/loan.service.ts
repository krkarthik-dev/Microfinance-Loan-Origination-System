import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, delay, of } from 'rxjs';

export interface LoanApplication {
  id: string;
  name: string;
  age: number;
  income: number;
  loanAmount: number;
  status: 'Pending' | 'Approved' | 'Rejected';
  score?: number;
  riskTier?: 'Excellent' | 'Good' | 'Fair' | 'Poor';
  pod?: number;
}

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  // Simulating an RxJS observable stream that would typically connect to a WebSocket or HTTP polling
  private loansSource = new BehaviorSubject<LoanApplication[]>([]);
  public loans$ = this.loansSource.asObservable();

  constructor() {
    // Initial mock data
    this.loansSource.next([
      { id: 'L-1001', name: 'John Doe', age: 45, income: 100000, loanAmount: 10000, status: 'Pending', score: 850, riskTier: 'Excellent', pod: 0.08 },
      { id: 'L-1002', name: 'Jane Smith', age: 22, income: 30000, loanAmount: 20000, status: 'Pending', score: 450, riskTier: 'Poor', pod: 0.75 }
    ]);
  }

  submitApplication(application: Partial<LoanApplication>): Observable<boolean> {
    // Simulate HTTP Post with RxJS
    const newLoan: LoanApplication = {
      id: `L-${Math.floor(1000 + Math.random() * 9000)}`,
      name: 'New Applicant',
      age: application.age!,
      income: application.income!,
      loanAmount: application.loanAmount!,
      status: 'Pending',
      // Mocking the ML Engine response for demonstration
      score: 680,
      riskTier: 'Good',
      pod: 0.35
    };
    
    const currentLoans = this.loansSource.getValue();
    this.loansSource.next([...currentLoans, newLoan]);
    
    return of(true).pipe(delay(500)); // Simulate network latency
  }

  updateLoanStatus(id: string, status: 'Approved' | 'Rejected'): Observable<boolean> {
    const currentLoans = this.loansSource.getValue();
    const updatedLoans = currentLoans.map(loan => 
      loan.id === id ? { ...loan, status } : loan
    );
    this.loansSource.next(updatedLoans);
    return of(true).pipe(delay(300));
  }
}
