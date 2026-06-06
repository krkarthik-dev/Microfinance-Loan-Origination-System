import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ApplicantDashboardComponent } from './applicant-dashboard/applicant-dashboard.component';
import { LoanOfficerDashboardComponent } from './loan-officer-dashboard/loan-officer-dashboard.component';

const routes: Routes = [
  { path: 'applicant', component: ApplicantDashboardComponent },
  { path: 'officer', component: LoanOfficerDashboardComponent },
  { path: '', redirectTo: '/applicant', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
