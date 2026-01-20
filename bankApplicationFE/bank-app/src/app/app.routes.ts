import { Routes } from '@angular/router';
import { LoginComponent } from './Loginpage/login/login.component';
import { DashboardComponent } from './Loginpage/userdetails/userdetails/userpage/userpage';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent } 
];