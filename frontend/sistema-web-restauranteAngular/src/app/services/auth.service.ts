import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { API_ROUTES } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = API_ROUTES.AUTH;

  constructor(private http: HttpClient, private router: Router) {}

login(username: string, password: string) {
    // Ahora enviamos un objeto JSON plano
    const body = { username, password };

    return this.http.post<any>(`${this.apiUrl}/login`, body).pipe(
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
  // Limpiamos el prefijo "ROLE_" por si Spring Security lo envía así (ej: "ROLE_ADMIN" -> "ADMIN")
  const cleanRol = rol ? rol.replace('ROLE_', '') : '';

  if (cleanRol === 'ADMIN') {
    this.router.navigate(['/admin']);
  } else if (cleanRol === 'MESERO') {
    this.router.navigate(['/mesero']);
  } else if (cleanRol === 'COCINA') {
    this.router.navigate(['/cocina']);
  } else if (cleanRol === 'CAJERO') {
    this.router.navigate(['/caja']);
  } else {
    this.router.navigate(['/']);
  }
}
}