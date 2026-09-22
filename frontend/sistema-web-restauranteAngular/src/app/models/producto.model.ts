import { Categoria } from './categoria.model';
export interface Producto {
  idComida: number;
  nombre: string;
  descripcion: string;
  precioUni: number;
  disponible: boolean;
  categoria: Categoria; // ✅ IMPORTANTE
}


