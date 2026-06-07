import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  error: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (res) => {
          // Decode JWT here to get role, for now just basic redirect
          // We can parse payload using atob
          try {
            const payload = JSON.parse(atob(res.token.split('.')[1]));
            if (payload.role === 'ROLE_LOAN_OFFICER') {
              this.router.navigate(['/officer-dashboard']);
            } else if (payload.role === 'ROLE_APPLICANT') {
              this.router.navigate(['/applicant-dashboard']);
            } else {
              this.router.navigate(['/']);
            }
          } catch(e) {
            this.router.navigate(['/']);
          }
        },
        error: (err) => {
          this.error = 'Invalid credentials. Please try again.';
        }
      });
    }
  }
}
