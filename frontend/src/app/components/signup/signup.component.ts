import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  signupForm: FormGroup;
  error: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.signupForm = this.fb.group({
      fullName: ['', Validators.required],
      phoneNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(g: FormGroup) {
    return g.get('password')?.value === g.get('confirmPassword')?.value
      ? null : { mismatch: true };
  }

  onSubmit() {
    if (this.signupForm.valid) {
      const userData = {
        fullName: this.signupForm.value.fullName,
        phoneNumber: this.signupForm.value.phoneNumber,
        email: this.signupForm.value.email,
        password: this.signupForm.value.password,
        role: 'ROLE_APPLICANT' // default role for customers
      };
      
      this.authService.register(userData).subscribe({
        next: (res) => {
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.error = 'Registration failed. ' + (err.error?.message || 'Please try again.');
        }
      });
    }
  }
}
