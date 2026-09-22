import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mesa } from '../models/mesa.model';

@Injectable({ providedIn: 'root' })
export class MesaService {

  private apiUrl = 'http://localhost:8081/api/mesas';

  constructor(private http: HttpClient) {}

  listar(): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(this.apiUrl);
  }

  crear(mesa: Mesa): Observable<Mesa> {
    return this.http.post<Mesa>(`${this.apiUrl}/nuevo`, mesa);
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