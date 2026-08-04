export interface Cliente {
  idCliente?: number;
  nombre: string;
  documento: string;
  tipoDocumento: 'DNI' | 'RUC' | 'CE';
  direccion?: string;
  correo?: string;
  telefono?: string;
}