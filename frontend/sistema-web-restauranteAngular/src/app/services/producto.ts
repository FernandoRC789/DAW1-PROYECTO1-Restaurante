import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from '../models/producto.model';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

 private apiUrl = 'http://localhost:8081/api/comidas';


  constructor(private http: HttpClient) {}

  // 📋 LISTAR TODOS
getProductos(): Observable<Producto[]> {
  return this.http.get<Producto[]>(this.apiUrl, {
    withCredentials: true
  });
}

  // 🔍 OBTENER POR ID
  getProductoById(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/${id}`);
  }

  // ➕ CREAR
  createProducto(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(this.apiUrl, producto);
  }

  // ✏️ ACTUALIZAR
  updateProducto(id: number, producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiUrl}/${id}`, producto);
  }

  // ❌ ELIMINAR
  deleteProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // 🔥 CAMBIAR DISPONIBILIDAD
  cambiarEstado(id: number, estado: boolean): Observable<Producto> {
    return this.http.patch<Producto>(`${this.apiUrl}/${id}/estado?estado=${estado}`, {});
  }

  // ✅ DISPONIBLES
  getDisponibles(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}/disponibles`);
  }

  // 🔍 BUSCAR POR NOMBRE
  buscarPorNombre(nombre: string): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}/buscar?nombre=${nombre}`);
  }

  // 🏷️ POR CATEGORIA
  buscarPorCategoria(id: number): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}/categoria/${id}`);
  }
}
