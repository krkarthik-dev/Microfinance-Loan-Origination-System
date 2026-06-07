import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { ApplicantOnboardingComponent } from './components/applicant-onboarding/applicant-onboarding.component';
import { ApplicantDashboardComponent } from './applicant-dashboard/applicant-dashboard.component';
import { LoanOfficerDashboardComponent } from './loan-officer-dashboard/loan-officer-dashboard.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'onboarding', component: ApplicantOnboardingComponent },
  { path: 'applicant-dashboard', component: ApplicantDashboardComponent },
  { path: 'officer-dashboard', component: LoanOfficerDashboardComponent },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
