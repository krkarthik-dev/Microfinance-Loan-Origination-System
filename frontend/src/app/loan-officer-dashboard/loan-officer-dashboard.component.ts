import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { LoanService, LoanApplication } from '../loan.service';

@Component({
  selector: 'app-loan-officer-dashboard',
  templateUrl: './loan-officer-dashboard.component.html',
  styleUrls: ['./loan-officer-dashboard.component.css']
})
export class LoanOfficerDashboardComponent implements OnInit {
  loans$: Observable<LoanApplication[]>;
  displayedColumns: string[] = ['id', 'name', 'details', 'score', 'riskTier', 'status', 'actions'];

  constructor(private loanService: LoanService) {
    this.loans$ = this.loanService.loans$;
  }

  ngOnInit(): void {}

  approveLoan(id: string) {
    this.loanService.updateLoanStatus(id, 'Approved').subscribe();
  }

  rejectLoan(id: string) {
    this.loanService.updateLoanStatus(id, 'Rejected').subscribe();
  }
}
