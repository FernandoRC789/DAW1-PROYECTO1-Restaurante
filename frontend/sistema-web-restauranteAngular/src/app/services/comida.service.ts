import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_ROUTES } from '../config/api.config';

export interface Comida {
  idComida: number;
  nombre: string;
  descripcion: string;
  precioUni: number;
  disponible: boolean;
  categoria: any;
}

@Injectable({
  providedIn: 'root'
})
export class ComidaService {

  private apiUrl = API_ROUTES.COMIDAS;

  constructor(private http: HttpClient) {}

  // 📋 LISTAR TODAS
  listar(): Observable<Comida[]> {
    return this.http.get<Comida[]>(this.apiUrl);
  }

  // 🔍 BUSCAR POR NOMBRE
  buscar(nombre: string): Observable<Comida[]> {
    return this.http.get<Comida[]>(`${this.apiUrl}/buscar?nombre=${nombre}`);
  }

  // 🏷️ FILTRAR POR CATEGORIA
  porCategoria(idCat: any): Observable<Comida[]> {
    return this.http.get<Comida[]>(`${this.apiUrl}/categoria/${idCat}`);
  }

  // ➕ CREAR
  crear(data: Comida): Observable<Comida> {
    return this.http.post<Comida>(`${this.apiUrl}`, data);
  }

  // ✏️ ACTUALIZAR
  actualizar(id: number, data: Comida): Observable<Comida> {
    return this.http.put<Comida>(`${this.apiUrl}/${id}`, data);
  }

  // ❌ ELIMINAR
  eliminar(id: number) {
    return this.http.delete(`${this.apiUrl}/${id}`, {
      responseType: 'text' // 🔥 IMPORTANTE por tu backend
    });
  }

  // 🔥 CAMBIAR DISPONIBILIDAD
  cambiarDisponible(id: number, estado: boolean): Observable<Comida> {
    return this.http.patch<Comida>(
      `${this.apiUrl}/${id}/disponible?estado=${estado}`,
      {}
    );
  }

  // ✅ SOLO DISPONIBLES
  disponibles(): Observable<Comida[]> {
    return this.http.get<Comida[]>(`${this.apiUrl}/disponibles`);
  }

    // ✅ SOLO NO DISPONIBLES
  noDisponibles(): Observable<Comida[]> {
    return this.http.get<Comida[]>(`${this.apiUrl}/no-disponibles`);
  }
}