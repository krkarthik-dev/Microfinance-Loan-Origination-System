import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-applicant-onboarding',
  templateUrl: './applicant-onboarding.component.html',
  styleUrls: ['./applicant-onboarding.component.css']
})
export class ApplicantOnboardingComponent {
  onboardingForm: FormGroup;
  submitting = false;
  successMsg = '';
  errorMsg = '';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.onboardingForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      aadhaarNumber: ['', [Validators.required, Validators.pattern('^[0-9]{12}$')]],
      panNumber: ['', [Validators.required, Validators.pattern('^[A-Z]{5}[0-9]{4}[A-Z]{1}$')]],
      dateOfBirth: ['', Validators.required],
      address: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.onboardingForm.valid) {
      this.submitting = true;
      this.errorMsg = '';
      this.successMsg = '';
      
      this.http.post('http://localhost:8080/api/applicants', this.onboardingForm.value)
        .subscribe({
          next: (res) => {
            this.submitting = false;
            this.successMsg = 'Profile created successfully!';
            setTimeout(() => {
              this.router.navigate(['/applicant-dashboard']);
            }, 2000);
          },
          error: (err) => {
            this.submitting = false;
            this.errorMsg = 'Failed to create profile. Please check the details.';
          }
        });
    }
  }
}
