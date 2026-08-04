import { Categoria } from '../models/categoria.model';

export interface Comida {
  idComida: number;
  nombre: string;
  descripcion: string; // 🔥 AGREGAR
  precioUni: number;
  disponible: boolean;
  categoria: Categoria; // ✅ AQUÍ VA
}