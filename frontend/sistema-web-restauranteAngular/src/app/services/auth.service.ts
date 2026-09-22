import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8081';

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string) {
    const body = new URLSearchParams();
    body.set('username', username);
    body.set('password', password);

    return this.http.post<any>(`${this.apiUrl}/login`, body.toString(), {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      withCredentials: true
    }).pipe(
      tap((res: any) => {
        // 🔥 guardar rol
        localStorage.setItem('rol', res.rol);

        // 🔥 redirección automática
        this.redirectByRole(res.rol);
      })
    );
  }

logout() {
  this.http.post(`${this.apiUrl}/logout`, {}, {
    withCredentials: true
  }).subscribe({
    next: () => {
      localStorage.clear();
      this.router.navigate(['/login']); // ✅ correcto
    },
    error: (err) => {
      console.error('Logout error', err);
    }
  });
}

  getRol(): string | null {
    return localStorage.getItem('rol');
  }

  isLogged(): boolean {
    return !!localStorage.getItem('rol');
  }

  redirectByRole(rol: string) {
    if (rol === 'ROLE_ADMIN') this.router.navigate(['/admin']);
    else if (rol === 'ROLE_MESERO') this.router.navigate(['/mesero']);
    else if (rol === 'ROLE_COCINA') this.router.navigate(['/cocina']);
    else if (rol === 'ROLE_CAJERO') this.router.navigate(['/caja']);
    else this.router.navigate(['/']);
  }
}