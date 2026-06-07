import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SignupComponent } from './signup.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;
  let mockAuthService: any;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['register']);

    await TestBed.configureTestingModule({
      declarations: [ SignupComponent ],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([{ path: 'login', redirectTo: '', pathMatch: 'full' }]),
        BrowserAnimationsModule
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when empty', () => {
    expect(component.signupForm.valid).toBeFalsy();
  });

  it('should validate full name requirement', () => {
    const fullNameControl = component.signupForm.get('fullName');
    fullNameControl?.setValue('');
    expect(fullNameControl?.hasError('required')).toBeTruthy();

    fullNameControl?.setValue('John Doe');
    expect(fullNameControl?.hasError('required')).toBeFalsy();
  });

  it('should validate phone number format', () => {
    const phoneControl = component.signupForm.get('phoneNumber');
    phoneControl?.setValue('123'); // Invalid
    expect(phoneControl?.hasError('pattern')).toBeTruthy();

    phoneControl?.setValue('1234567890'); // Valid
    expect(phoneControl?.hasError('pattern')).toBeFalsy();
  });

  it('should validate email format', () => {
    const emailControl = component.signupForm.get('email');
    emailControl?.setValue('invalid-email');
    expect(emailControl?.hasError('email')).toBeTruthy();

    emailControl?.setValue('test@example.com');
    expect(emailControl?.hasError('email')).toBeFalsy();
  });

  it('should validate password mismatch', () => {
    component.signupForm.patchValue({
      password: 'password123',
      confirmPassword: 'differentpassword'
    });
    expect(component.signupForm.hasError('mismatch')).toBeTruthy();

    component.signupForm.patchValue({
      confirmPassword: 'password123'
    });
    expect(component.signupForm.hasError('mismatch')).toBeFalsy();
  });

  it('should call authService.register on valid form submission', () => {
    component.signupForm.patchValue({
      fullName: 'John Doe',
      phoneNumber: '1234567890',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });

    mockAuthService.register.and.returnValue(of({ message: 'Success' }));
    component.onSubmit();

    expect(mockAuthService.register).toHaveBeenCalledWith({
      fullName: 'John Doe',
      phoneNumber: '1234567890',
      email: 'test@example.com',
      password: 'password123',
      role: 'ROLE_APPLICANT'
    });
  });
});
