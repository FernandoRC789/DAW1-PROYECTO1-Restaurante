import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mesa } from '../models/mesa.model';
import { API_ROUTES } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class MesaService {

  private apiUrl = API_ROUTES.MESAS;

  constructor(private http: HttpClient) {}

  listar(): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(this.apiUrl);
  }

  crear(mesa: Mesa): Observable<Mesa> {
    return this.http.post<Mesa>(`${this.apiUrl}`, mesa);
  }

  actualizar(id: number, mesa: Mesa): Observable<Mesa> {
    return this.http.put<Mesa>(`${this.apiUrl}/${id}`, mesa);
  }

  eliminar(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  cambiarEstado(id: number, estado: string): Observable<Mesa> {
    return this.http.patch<Mesa>(`${this.apiUrl}/${id}/estado?estado=${estado}`, {});
  }
}