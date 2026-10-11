import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Cliente } from '../models/cliente.model';
import { Observable } from 'rxjs';
import { API_ROUTES } from '../config/api.config';


@Injectable({ providedIn: 'root' })
export class ClienteService {

  private api = API_ROUTES.CLIENTES;

  constructor(private http: HttpClient) {}

  // 🔎 buscar por documento
  buscarPorDocumento(doc: string) {
    return this.http.get<Cliente>(`${this.api}/documento/${doc}`);
  }

  // ➕ crear cliente
  crear(cliente: Cliente):  Observable<Cliente>{
    return this.http.post<Cliente>(`${this.api}`, cliente);
  }

  // ✏️ actualizar cliente
  actualizar(cliente: Cliente) {
    return this.http.put<Cliente>(`${this.api}/${cliente.idCliente}`, cliente);
  }

  // 📄 listar (opcional)
  listar() {
    return this.http.get<Cliente[]>(this.api);
  }
}