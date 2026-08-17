import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { PedidoDTO } from "../models/pedido.model";

@Injectable({ providedIn: 'root' })
export class PedidoService {

  private apiUrl = 'http://localhost:8081/api/pedidos';

  constructor(private http: HttpClient) {}

  // 🧾 CREAR PEDIDO
  crear(dto: PedidoDTO) {
    return this.http.post(`${this.apiUrl}/nuevo`, dto);
  }

  misPedidos() {
    return this.http.get<any[]>(`${this.apiUrl}/mis-pedidos`);
  }

  // ✏️ ACTUALIZAR
  actualizar(id: number, dto: PedidoDTO) {
    return this.http.put(`${this.apiUrl}/${id}`, dto);
  }

  listar() {
    return this.http.get<any[]>(`${this.apiUrl}`);
  }

cambiarEstado(id: number, estadoId: number) {
  return this.http.patch(
    `${this.apiUrl}/${id}/estado?estadoId=${estadoId}`,
    {}
  );
}
}