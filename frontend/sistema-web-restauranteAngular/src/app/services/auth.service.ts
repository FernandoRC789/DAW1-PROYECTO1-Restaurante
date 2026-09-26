import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8082';

  constructor(private http: HttpClient, private router: Router) {}

login(username: string, password: string) {
    // Ahora enviamos un objeto JSON plano
    const body = { username, password };

    return this.http.post<any>(`${this.apiUrl}/api/auth/login`, body).pipe(
      tap((res: any) => {
        // 🔥 Guardamos el token JWT y el rol principal en el localStorage
        localStorage.setItem('token', res.token);
        localStorage.setItem('rol', res.roles[0]); // Ej: "ROLE_ADMIN"

        // 🔥 Redirección automática según el rol
        this.redirectByRole(res.roles[0]);
      })
    );
  }

logout() {
    // En arquitectura Stateless (JWT), el logout es local: limpiamos el navegador
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRol(): string | null {
    return localStorage.getItem('rol');
  }

  isLogged(): boolean {
    return !!localStorage.getItem('token'); // Verificamos si existe el token
  }

  redirectByRole(rol: string) {
    if (rol === 'ROLE_ADMIN') this.router.navigate(['/admin']);
    else if (rol === 'ROLE_MESERO') this.router.navigate(['/mesero']);
    else if (rol === 'ROLE_COCINA') this.router.navigate(['/cocina']);
    else if (rol === 'ROLE_CAJERO') this.router.navigate(['/caja']);
    else this.router.navigate(['/']);
  }
}