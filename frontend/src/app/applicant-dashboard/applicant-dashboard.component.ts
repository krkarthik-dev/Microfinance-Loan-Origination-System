import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoanService } from '../loan.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-applicant-dashboard',
  templateUrl: './applicant-dashboard.component.html',
  styleUrls: ['./applicant-dashboard.component.css']
})
export class ApplicantDashboardComponent {
  applicationForm: FormGroup;
  isSubmitting = false;
  successMessage = '';
  selectedAadhaar: File | null = null;
  selectedPan: File | null = null;

  constructor(
    private fb: FormBuilder, 
    private loanService: LoanService,
    private authService: AuthService
  ) {
    this.applicationForm = this.fb.group({
      age: ['', [Validators.required, Validators.min(18)]],
      income: ['', [Validators.required, Validators.min(1000)]],
      loanAmount: ['', [Validators.required, Validators.min(100)]]
    });
  }

  logout() {
    this.authService.logout();
  }

  onFileSelected(event: any, type: 'aadhaar' | 'pan') {
    const file: File = event.target.files[0];
    if (file) {
      if (type === 'aadhaar') this.selectedAadhaar = file;
      else this.selectedPan = file;
    }
  }

  onSubmit() {
    if (this.applicationForm.valid) {
      this.isSubmitting = true;
      this.loanService.submitApplication(this.applicationForm.value).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.successMessage = 'Loan Application Submitted Successfully! KYC Uploaded.';
          this.applicationForm.reset();
          this.selectedAadhaar = null;
          this.selectedPan = null;
        },
        error: (err) => {
          console.error(err);
          this.isSubmitting = false;
        }
      });
    }
  }
}
