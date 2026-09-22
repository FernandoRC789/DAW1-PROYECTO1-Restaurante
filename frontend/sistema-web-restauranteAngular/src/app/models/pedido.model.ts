export interface DetallePedidoDTO {
  comidaId: number;
  cantidad: number;
}

export interface PedidoDTO {
  estadoId: number;
  mesaId: number;
  observaciones?: string;
  detalle: DetallePedidoDTO[];
}