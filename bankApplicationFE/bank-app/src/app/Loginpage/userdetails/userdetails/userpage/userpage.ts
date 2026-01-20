// import { Component, OnInit } from '@angular/core';
// import { ActivatedRoute } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { LoginService } from '../../../login.service';
// import { Router } from '@angular/router';

// @Component({
//   standalone: true,
//   selector: 'app-dashboard',
//   imports: [CommonModule, FormsModule],
//   templateUrl: './userpage.html'
// })
// export class DashboardComponent implements OnInit {
//   user: any;

//   addAmount = 0;
//   sendAccount = '';
//   sendAmount = 0;

//   showAddMoney = false;
//   showSendMoney = false;
//   showBankDetails = true;
//   showDownloadPdf = false;

//   fromDate!: string;
//   toDate!: string;
//   password: string = '';
//   addPassword: string = '';

//   constructor(
//     private route: ActivatedRoute,
//     private loginService: LoginService,
//     private router: Router
//   ) {}

  
//   ngOnInit() {
//     const email = this.route.snapshot.queryParamMap.get('email');
//     if (email) {
//       this.loginService.getUserByEmail(email).subscribe(res => {
//         this.user = res;
//       });
//     }
//   }

//   openAddMoney() {
//     this.showAddMoney = true;
//     this.showSendMoney = false;
//   }

//   openSendMoney() {
//     this.showSendMoney = true;
//     this.showAddMoney = false;
//   }

//   closePanels() {
//     this.showAddMoney = false;
//     this.showSendMoney = false;
//   }

//   openDownloadPdf() {
//   this.resetViews();
//   this.showDownloadPdf = true;
// }

//   resetViews() {
//   this.showBankDetails = false;
//   this.showAddMoney = false;
//   this.showSendMoney = false;
//   this.showDownloadPdf = false;
// }

//   selectTab(tab: string) {
//   this.showBankDetails = false;
//   this.showAddMoney = false;
//   this.showSendMoney = false;
//   this.showDownloadPdf = false;

//   if (tab === 'bank') this.showBankDetails = true;
//   if (tab === 'add') this.showAddMoney = true;
//   if (tab === 'send') this.showSendMoney = true;
//   if (tab === 'pdf') this.showDownloadPdf = true;
// }

//   addMoney() {
//   if (this.addAmount <= 0) {
//     alert('Enter valid amount');
//     return;
//   }

//   if (!this.addPassword) {
//     alert('Password is required');
//     return;
//   }

//   this.loginService.addMoney({
//     accountNumber: this.user.accountNumber,
//     amount: this.addAmount,
//     password: this.addPassword
//   }).subscribe((res: any) => {

//     alert(res);

//     if (res.includes('successful')) {
//       this.user.balance += this.addAmount;
//       this.addAmount = 0;
//       this.addPassword = '';
//       this.showAddMoney = false;
//     }
//   });
// }


// //   sendMoney() {
// //   if (!this.sendAccount || this.sendAmount <= 0) {
// //     return alert('Enter valid details');
// //   }

// //   this.loginService.sendMoney({
// //     fromAccount: this.user.accountNumber, 
// //     toAccount: this.sendAccount,           
// //     amount: this.sendAmount
// //   }).subscribe(res => {
// //     alert(res);
// //     this.user.balance -= this.sendAmount;
// //     this.sendAccount = '';
// //     this.sendAmount = 0;
// //     this.closePanels();
// //   });
// // }

//   sendMoney() {

//   if (!this.sendAccount || this.sendAmount <= 0) {
//     alert('Enter valid account and amount');
//     return;
//   }

//   if (!this.password) {
//     alert('Password is required');
//     return;
//   }

//   this.loginService.sendMoney({
//     fromAccount: this.user.accountNumber,
//     toAccount: this.sendAccount,
//     amount: this.sendAmount,
//     password: this.password
//   }).subscribe(res => {

//     alert(res);

//     if (res.includes('successful')) {
//       this.user.balance -= this.sendAmount;
//       this.sendAccount = '';
//       this.sendAmount = 0;
//       this.password = '';
//       this.showSendMoney = false;
//     }
//   });
// }

//  downloadPDF() {
//   if (!this.fromDate || !this.toDate) {
//     alert('Please select From Date and To Date');
//     return;
//   }

//   this.loginService.downloadTransactionsPdf(
//     this.user.accountNumber,
//     this.fromDate,
//     this.toDate
//   );
// }

// // downloadCSV() {
// //   this.loginService.downloadTransactionsCsv(this.user.accountNumber);
// // }

// logout() {
//   const confirmLogout = window.confirm('Shall we logout?');
//   if (confirmLogout) {
//     // 1️⃣ Clear any session or localStorage (if you are storing user info)
//     localStorage.removeItem('user'); // or remove your auth token

//     // 2️⃣ Reset component state (optional)
//     this.user = null;

//     // 3️⃣ Navigate to login page
//     this.router.navigate(['/login']);
//   } else {
//     // Do nothing, stay on the dashboard
//     console.log('Logout cancelled');
//   }
// }



// }

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../../login.service';

@Component({
  standalone: true,
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './userpage.html'
})
export class DashboardComponent implements OnInit {
  user: any;

  addAmount = 0;
  sendAccount = '';
  sendAmount = 0;

  showAddMoney = false;
  showSendMoney = false;
  showBankDetails = true;
  showDownloadPdf = false;

  fromDate!: string;
  toDate!: string;
  password: string = '';
  addPassword: string = '';

  constructor(
    private route: ActivatedRoute,
    private loginService: LoginService,
    private router: Router
  ) {}

  ngOnInit() {
    // Get user info from route or localStorage
    const email = this.route.snapshot.queryParamMap.get('email');
    if (email) {
      this.loginService.getUserByEmail(email).subscribe(res => {
        this.user = res;
      });
    } else {
      // fallback: try from localStorage
      const savedUser = localStorage.getItem('user');
      if (savedUser) this.user = JSON.parse(savedUser);
    }
  }

  selectTab(tab: string) {
    this.resetViews();
    if (tab === 'bank') this.showBankDetails = true;
    if (tab === 'add') this.showAddMoney = true;
    if (tab === 'send') this.showSendMoney = true;
    if (tab === 'pdf') this.showDownloadPdf = true;
  }

  resetViews() {
    this.showBankDetails = false;
    this.showAddMoney = false;
    this.showSendMoney = false;
    this.showDownloadPdf = false;
  }

  addMoney() {
    if (this.addAmount <= 0) return alert('Enter valid amount');
    if (!this.addPassword) return alert('Password is required');

    this.loginService.addMoney({
      accountNumber: this.user.accountNumber,
      amount: this.addAmount,
      password: this.addPassword
    }).subscribe((res: any) => {
      alert(res);
      if (res.includes('successful')) {
        this.user.balance += this.addAmount;
        this.addAmount = 0;
        this.addPassword = '';
        this.showAddMoney = false;
      }
    });
  }

  sendMoney() {
    if (!this.sendAccount || this.sendAmount <= 0)
      return alert('Enter valid account and amount');
    if (!this.password) return alert('Password is required');

    this.loginService.sendMoney({
      fromAccount: this.user.accountNumber,
      toAccount: this.sendAccount,
      amount: this.sendAmount,
      password: this.password
    }).subscribe(res => {
      alert(res);
      if (res.includes('successful')) {
        this.user.balance -= this.sendAmount;
        this.sendAccount = '';
        this.sendAmount = 0;
        this.password = '';
        this.showSendMoney = false;
      }
    });
  }

  downloadPDF() {
    if (!this.fromDate || !this.toDate)
      return alert('Please select From Date and To Date');

    this.loginService.downloadTransactionsPdf(
      this.user.accountNumber,
      this.fromDate,
      this.toDate
    );
  }

  logout() {
  const confirmLogout = window.confirm('Shall we logout?');

  if (confirmLogout) {
    localStorage.clear();   

    this.router.navigate(['']);   
  }
}

}
