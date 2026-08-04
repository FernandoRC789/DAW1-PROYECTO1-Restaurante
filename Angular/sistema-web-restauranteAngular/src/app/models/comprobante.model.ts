export interface Comprobante {
  idComprobantePago: number;
  numero: string;
  total: number;

  metodoPago: string;

  estado: {
    nombre: string;
  };

  pedido: {
    idPedido: number;
    total: number;
  };

  cliente?: {
    idCliente: number;
    nombre?: string;
    tipoDocumento: string;
    documento:string;
  };

  fechaEmision: string;
  tipoComprobante: string;

}

export interface DetalleComprobante {
  producto: string;
  cantidad: number;
  precio: number;
}

export interface ComprobanteResponse {
  numero: string;
  fechaEmision: string;
  total: number;
  metodoPago: string;
  tipoComprobante: string;

  clienteNombre: string;
  clienteDocumento: string;

  detalles: DetalleComprobante[];
}