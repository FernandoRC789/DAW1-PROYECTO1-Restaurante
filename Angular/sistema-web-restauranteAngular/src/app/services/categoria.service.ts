import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Categoria {
  idCat: number;
  nombre: string;
}

@Injectable({ providedIn: 'root' })
export class CategoriaService {

  private apiUrl = 'http://localhost:8080/api/categorias';

  constructor(private http: HttpClient) {}

    getCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl);
  }

  // ✅ LISTAR
  listar(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl);
  }

  // ✅ CREAR
  crear(categoria: Categoria): Observable<Categoria> {
    return this.http.post<Categoria>(`${this.apiUrl}/nuevo`, categoria);
  }

  // ✅ ACTUALIZAR
  actualizar(id: number, categoria: Categoria): Observable<Categoria> {
    return this.http.put<Categoria>(`${this.apiUrl}/${id}`, categoria);
  }

  // ✅ ELIMINAR
  eliminar(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, {
      responseType: 'text' // 🔥 evita error tipo "no es JSON"
    });
  }

  buscar(nombre: string) {
  return this.http.get<Categoria[]>(
    `${this.apiUrl}/buscar?nombre=${nombre}`
  );
}

}