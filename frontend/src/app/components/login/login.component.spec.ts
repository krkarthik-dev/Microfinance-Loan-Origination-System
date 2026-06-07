import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: any;
  let router: Router;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['login']);

    await TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([
          { path: 'officer-dashboard', redirectTo: '', pathMatch: 'full' },
          { path: 'applicant-dashboard', redirectTo: '', pathMatch: 'full' },
          { path: '', redirectTo: '', pathMatch: 'full' }
        ])
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when empty', () => {
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should have valid form when filled', () => {
    component.loginForm.patchValue({
      email: 'test@example.com',
      password: 'password123'
    });
    expect(component.loginForm.valid).toBeTruthy();
  });

  it('should navigate to applicant dashboard on successful login as applicant', () => {
    const navigateSpy = spyOn(router, 'navigate');
    
    // Create a mock JWT token with ROLE_APPLICANT
    const payload = btoa(JSON.stringify({ role: 'ROLE_APPLICANT' }));
    const mockToken = `header.${payload}.signature`;
    
    mockAuthService.login.and.returnValue(of({ token: mockToken }));

    component.loginForm.patchValue({
      email: 'test@example.com',
      password: 'password123'
    });

    component.onSubmit();

    expect(mockAuthService.login).toHaveBeenCalledWith({ email: 'test@example.com', password: 'password123' });
    expect(navigateSpy).toHaveBeenCalledWith(['/applicant-dashboard']);
  });

  it('should navigate to officer dashboard on successful login as officer', () => {
    const navigateSpy = spyOn(router, 'navigate');
    
    // Create a mock JWT token with ROLE_LOAN_OFFICER
    const payload = btoa(JSON.stringify({ role: 'ROLE_LOAN_OFFICER' }));
    const mockToken = `header.${payload}.signature`;
    
    mockAuthService.login.and.returnValue(of({ token: mockToken }));

    component.loginForm.patchValue({
      email: 'officer@example.com',
      password: 'password123'
    });

    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith(['/officer-dashboard']);
  });

  it('should display error message on login failure', () => {
    mockAuthService.login.and.returnValue(throwError(() => new Error('Invalid credentials')));

    component.loginForm.patchValue({
      email: 'test@example.com',
      password: 'wrongpassword'
    });

    component.onSubmit();

    expect(component.error).toBe('Invalid credentials. Please try again.');
  });
});
