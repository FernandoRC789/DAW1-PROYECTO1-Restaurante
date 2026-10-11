import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { API_ROUTES } from "../config/api.config";

import {ComprobanteResponse,DetalleComprobante, Comprobante } from "../models/comprobante.model";
import { ComprobanteRequest } from "../models/comprobante.request.model";

@Injectable({ providedIn: 'root' })
export class CajaService {

  private api = API_ROUTES.COMPROBANTES;

  constructor(private http: HttpClient) {}

  // ==========================
  // 📄 LISTAR TODOS
  // ==========================
  listar(): Observable<Comprobante[]> {
    return this.http.get<Comprobante[]>(this.api);
  }

  // ==========================
  // 🔎 FILTROS
  // ==========================
  porNumero(numero: string): Observable<Comprobante[]> {
    return this.http.get<Comprobante[]>(`${this.api}/numero/${numero}`);
  }

  porCliente(id: number): Observable<Comprobante[]> {
    return this.http.get<Comprobante[]>(`${this.api}/cliente/${id}`);
  }

  porMetodo(metodo: string): Observable<Comprobante[]> {
    return this.http.get<Comprobante[]>(`${this.api}/metodo/${metodo}`);
  }

  // ==========================
  // 💰 GENERAR COMPROBANTE (POS)
  // ==========================
generar(data: ComprobanteRequest): Observable<ComprobanteResponse> {
  return this.http.post<ComprobanteResponse>(
    `${this.api}/generar`,
    data
  );
}

  // ==========================
  // ❌ ANULAR COMPROBANTE
  // ==========================
  anular(id: number): Observable<any> {
    return this.http.patch(`${this.api}/${id}/anular`, {});
  }
}