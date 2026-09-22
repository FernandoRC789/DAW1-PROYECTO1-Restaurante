import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Usuario {
  idUser: number;
  username: string;
  roles: { nombre: string }[];
}

@Injectable({ providedIn: 'root' })
export class UsuarioService {

  private api = 'http://localhost:8081/api/usuarios';

  constructor(private http: HttpClient) {}

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.api, { withCredentials: true });
  }

  crear(data: any) {
    return this.http.post(`${this.api}/nuevo`, data, { withCredentials: true });
  }

  actualizar(id: number, data: any) {
    return this.http.put(`${this.api}/${id}`, data, { withCredentials: true });
  }

  eliminar(id: number) {
    return this.http.delete(`${this.api}/${id}`, { withCredentials: true });
  }

  buscar(username: string) {
    return this.http.get<Usuario[]>(`${this.api}/buscar?username=${username}`, { withCredentials: true });
  }

  porRol(rol: string) {
    return this.http.get<Usuario[]>(`${this.api}/rol/${rol}`, { withCredentials: true });
  }

  contarPorRol(rol: string) {
    return this.http.get<number>(`${this.api}/rol/${rol}/count`, { withCredentials: true });
  }
}