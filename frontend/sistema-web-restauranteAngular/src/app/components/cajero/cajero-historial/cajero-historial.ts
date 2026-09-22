import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CajaService } from '../../../services/cajero.service';
import Swal from 'sweetalert2';
import { ChangeDetectorRef } from '@angular/core';



@Component({
  selector: 'app-cajero-historial-comprobantes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cajero-historial.html'
})
export class CajeroHistorialComprobantesComponent implements OnInit {

  comprobantes: any[] = [];
  loading = false;

  // 🔎 filtros
  filtroNumero = '';
  filtroDocumento = ''; // 🔥 antes era clienteId
  filtroMetodo = '';
  filtroTipo = ''; // 🔥 NUEVO

  comprobantesOriginal: any[] = []; // 🔥 NUEVO

  constructor(
    private cajaService: CajaService,
    private cdr: ChangeDetectorRef

  ) {}

  ngOnInit() {
    this.listar();
  }

  // ==========================
  // 📄 LISTAR
  // ==========================
  listar() {
    this.loading = true;

    this.cajaService.listar().subscribe({
      next: (data) => {
        this.comprobantes = data;
        this.comprobantesOriginal = data; // 🔥 guardas copia
        this.loading = false;
        if (data.length === 0) {
          Swal.fire({
            icon: 'info',
            title: 'Sin comprobantes',
            text: 'No hay registros disponibles'
          });
        }
                  this.cdr.detectChanges(); // 🔥 ESTE TE FALTABA

      },
      error: () => {
        this.loading = false;

        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudieron cargar los comprobantes'
        });
      }
    });
                      this.cdr.detectChanges(); // 🔥 ESTE TE FALTABA

  }

  // ==========================
  // 🔎 FILTRAR
  // ==========================
  /*filtrar() {

  Swal.fire({
    title: 'Buscando...',
    allowOutsideClick: false,
    didOpen: () => {
      Swal.showLoading();
    }
  });

  if (this.filtroNumero) {
    this.cajaService.porNumero(this.filtroNumero)
      .subscribe(this.handleResponse());
    return;
  }

  if (this.filtroClienteId) {
    this.cajaService.porCliente(this.filtroClienteId)
      .subscribe(this.handleResponse());
    return;
  }

  if (this.filtroMetodo) {
    this.cajaService.porMetodo(this.filtroMetodo)
      .subscribe(this.handleResponse());
    return;
  }

  Swal.close();
  this.listar();
}*/
filtrar() {

  this.loading = true;

  this.cajaService.listar().subscribe({
    next: (data) => {

    let resultado = [...this.comprobantesOriginal]; // 🔥 CLAVE

      if (this.filtroNumero) {
        resultado = resultado.filter(c =>
          c.numero?.toLowerCase().includes(this.filtroNumero.toLowerCase())
        );
      }

      if (this.filtroDocumento) {
        resultado = resultado.filter(c =>
          c.cliente?.documento?.includes(this.filtroDocumento)
        );
      }

      if (this.filtroMetodo) {
        resultado = resultado.filter(c =>
          c.metodoPago === this.filtroMetodo
        );
      }

      if (this.filtroTipo) {
        resultado = resultado.filter(c =>
          c.tipoComprobante === this.filtroTipo
        );
      }

      this.comprobantes = resultado;
      this.loading = false;

      if (resultado.length === 0) {
        Swal.fire({
          icon: 'info',
          title: 'Sin resultados',
          text: 'No se encontró ningún comprobante'
        });
      }
                            this.cdr.detectChanges(); // 🔥 ESTE TE FALTABA

    },

    error: () => {
      this.loading = false;

      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Error al filtrar'
      });
    }
  });
}

handleResponse() {
  return {
    next: (data: any[]) => {
      Swal.close();
      this.comprobantes = data;

      if (data.length === 0) {
        Swal.fire({
          icon: 'info',
          title: 'Sin resultados',
          text: 'No se encontró ningún comprobante'
        });
      }
    },
    error: () => {
        Swal.close(); // 🔥 te faltaba esto

      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Error al filtrar comprobantes'
      });
    }
  };
}
  /*filtrar() {

    /*if (this.filtroNumero) {
      this.cajaService.porNumero(this.filtroNumero)
        .subscribe(data => this.comprobantes = data);
      return;
    }*/
   /*Swal.fire({
      title: 'Buscando...',
      allowOutsideClick: false,
      didOpen: () => {
        Swal.showLoading();
      }
    });

    
    this.cajaService.porNumero(this.filtroNumero)
      .subscribe({
        next: (data) => {
          Swal.close();
          this.comprobantes = data;

          if (data.length === 0) {
            Swal.fire({
              icon: 'info',
              title: 'Sin resultados',
              text: 'No se encontró ningún comprobante'
            });
          }
        },
        error: () => {
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Error al filtrar comprobantes'
          });
        }
      });


    if (this.filtroClienteId) {
      this.cajaService.porCliente(this.filtroClienteId)
        .subscribe(data => this.comprobantes = data);
      return;
    }

    if (this.filtroMetodo) {
      this.cajaService.porMetodo(this.filtroMetodo)
        .subscribe(data => this.comprobantes = data);
      return;
    }

    this.listar();
  }*/

  // ==========================
  // ❌ ANULAR
  // ==========================
anular(id: number) {

  Swal.fire({
    title: '¿Anular comprobante?',
    text: 'Esta acción no se puede revertir',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Sí, anular',
    cancelButtonText: 'Cancelar'
  }).then((result) => {

    if (result.isConfirmed) {

      this.cajaService.anular(id).subscribe({
        next: () => {

          // ✅ Toast de éxito
          Swal.fire({
            toast: true,
            position: 'top-end',
            icon: 'success',
            title: 'Comprobante anulado',
            showConfirmButton: false,
            timer: 2000
          });

          this.listar(); // 🔥 refresca
        },

        error: (err) => {
          Swal.fire({
            icon: 'error',
            title: 'Error al anular',
            text: err?.error?.message || 'No se pudo anular el comprobante'
          });
        }
      });

    }

  });
}

  // ==========================
  // 🎨 BADGE ESTADO
  // ==========================
  getEstadoClase(estado: string) {
    if (estado === 'PAGADO') return 'bg-green';
    if (estado === 'ANULADO') return 'bg-red';
    return 'bg-gray';
  }
}