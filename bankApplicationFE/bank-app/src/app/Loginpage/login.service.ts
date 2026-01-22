import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BankDTO {
  name: string;
  email: string;
  password: string;
  confirmPassword: string;
  bankName: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  // Base URL of backend
  private baseUrl = 'http://localhost:8080/api'; 

  constructor(private http: HttpClient) {}

  createUser(dto: BankDTO): Observable<string> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(`${this.baseUrl}/saveusers`, dto, { headers, responseType: 'text' });
  }

  login(dto: { email: string; password: string }): Observable<string> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(`${this.baseUrl}/login`, dto, { headers, responseType: 'text' });
  }

  resendOtp(email: string): Observable<string> {
  const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  return this.http.post(
    `${this.baseUrl}/resendOtp`,
    { email },
    { headers, responseType: 'text' }
  );
}

  verifyOtp(dto: { email: string; otp: string }): Observable<string> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(`${this.baseUrl}/validate-otp`, dto, { headers, responseType: 'text' });
  }

  getUserByEmail(email: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/user?email=${email}`);
  }

//   addMoney(data: { accountNumber: string; amount: number }): Observable<string> {
//   return this.http.post(`${this.baseUrl}/addmoney`, data, { responseType: 'text' });
// }

  addMoney(data: {
  accountNumber: string;
  amount: number;
  password: string;
}) {
  return this.http.post(
    'http://localhost:8080/api/addmoney',
    data,
    { responseType: 'text' }
  );
}

//   sendMoney(dto: { fromAccount: string; toAccount: string; amount: number }): Observable<string> {
//     const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
//     return this.http.post(`${this.baseUrl}/sendamt`, dto, { headers, responseType: 'text' });
// }

  sendMoney(data: {
  fromAccount: string;
  toAccount: string;
  amount: number;
  password: string;
}) {
  return this.http.post('http://localhost:8080/api/sendamt', data,{ responseType: 'text'});
}
  downloadTransactionsPdf(
  accountNumber: string,
  fromDate: string,
  toDate: string
): void {

  const url =
    `http://localhost:8080/api/download/pdf` +
    `?accountNumber=${encodeURIComponent(accountNumber)}` +
    `&fromDate=${fromDate}` +
    `&toDate=${toDate}`;

  this.http.get(url, { responseType: 'blob' }).subscribe({
    next: (blob) => {
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(blob);
      a.href = objectUrl;
      a.download = `transactions_${accountNumber}.pdf`;
      a.click();
      URL.revokeObjectURL(objectUrl);
    },
    error: (err) => console.error('Download PDF failed', err)
  });
}



 downloadTransactionsCsv(accountNumber: string): void {
  const url = `${this.baseUrl}/download/csv?accountNumber=${encodeURIComponent(accountNumber)}`;
  this.http.get(url, { responseType: 'blob' }).subscribe({
    next: (blob) => {
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(blob);
      a.href = objectUrl;
      a.download = `transactions_${accountNumber}.csv`;
      a.click();
      URL.revokeObjectURL(objectUrl);
    },
    error: (err) => console.error('Download CSV failed', err)
  });
}


  
}