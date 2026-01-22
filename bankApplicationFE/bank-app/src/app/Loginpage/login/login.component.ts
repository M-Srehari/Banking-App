import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { LoginService } from '../login.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule
  ],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  userForm: FormGroup;
  otpForm: FormGroup;
  isRegisterView = true;
  isLoginView = false;
  isOtpView = false;
  resendDisabled = false;
  emailForOtp = '';

  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private router: Router
  ) {
    this.userForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
      bankName: ['', Validators.required]
    });

    this.otpForm = this.fb.group({
      otp: ['', Validators.required]
    });
  }

  showLogin() {
    this.isRegisterView = false;
    this.isLoginView = true;
    this.otpForm.reset();
    this.isOtpView = false;
  }

  saveUser() {
  if (this.userForm.invalid) {
    alert('Please fill all required fields');
    return;
  }

  this.loginService.createUser(this.userForm.value).subscribe({
    next: (res: any) => {

      if (res && res.includes('Account created successfully')) {
        alert(res);

        this.isRegisterView = false;
        this.isLoginView = true;
        this.isOtpView = false;

        const email = this.userForm.get('email')?.value;
        this.userForm.reset();
        this.userForm.patchValue({ email });

      } 
      else {
        alert(res); 
      }
    },
    error: (err: any) => {
      alert('Error creating user');
      console.error(err);
    }
  });
}

  loginUser() {
    const dto = {
      email: this.userForm.value.email,
      password: this.userForm.value.password
    };

    this.loginService.login(dto).subscribe({
      next: res => {
        if (res.includes('OTP sent')) {
  this.isLoginView = false;
  this.isOtpView = true;
  this.emailForOtp = dto.email;
} else {
  alert(res);
}
      },
      error: () => alert('Login failed')
    });
  }

  showRegister() {
  this.isRegisterView = true;
  this.isLoginView = false;
  this.isOtpView = false;
}

  verifyOtp() {
  const dto = {
    email: this.emailForOtp,
    otp: this.otpForm.value.otp
  };

  this.loginService.verifyOtp(dto).subscribe({
    next: res => {
      alert(res);

      if (res === 'Login successful') {
        this.router.navigate(
          ['/dashboard'],
          { queryParams: { email: this.emailForOtp } }
        );
      }
    },
    error: () => alert('OTP verification failed')
  });
}

resendOtp() {
  this.loginService.resendOtp(this.emailForOtp).subscribe({
    next: (res: any) => {
      alert(res);
      if (res.includes('successfully')) {
        this.resendDisabled = true;
      }
    },
    error: () => alert('Failed to resend OTP')
  });
}
}