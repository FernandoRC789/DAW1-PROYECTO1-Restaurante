import { EstadoMesa } from './estado-mesa.enum';

export interface Mesa {
  idMesa: number;
  numero: string;
  asientos: number;
  estado_mesa: EstadoMesa;
}