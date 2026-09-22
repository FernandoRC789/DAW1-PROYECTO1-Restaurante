export interface ComprobanteRequest {
  pedidoId: number;
  clienteId?: number;
  tipoComprobante: 'BOLETA' | 'FACTURA';
  metodoPago: 'EFECTIVO' | 'YAPE' | 'TARJETA';
}