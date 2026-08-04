import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { PedidoService } from '../../../services/pedido.service';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-mesero-historial',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mesero-historial.html'
})
export class MeseroHistorialComponent implements OnInit {

  pedidos: any[] = [];
  loading = false;

  //paginacion:
    pagina = 1;
  tamanoPagina = 8;

  constructor(
    private pedidoService: PedidoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.cargarHistorial();
  }

  // 📋 HISTORIAL
  cargarHistorial() {
    this.loading = true;

    Swal.fire({
  title: 'Cargando historial...',
  allowOutsideClick: false,
  didOpen: () => {
    Swal.showLoading();
  }
});
    this.pedidoService.misPedidos()
      .pipe(
        finalize(() => {
          Swal.close();
          this.loading = false;
          this.cdr.detectChanges(); // 🔥 fuerza actualización UI
        })
      )
      
      .subscribe({
        next: data => {
          if (data.length === 0) {
          Swal.fire({
            icon: 'info',
            title: 'Sin pedidos',
            text: 'Aún no tienes pedidos registrados'
          });
        }
          console.log("PEDIDOS:", data);
          this.pedidos = data;
        },
        error: () => {
          this.pedidos = [];

          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'No se pudo cargar el historial de pedidos'
          });
        }
      });
  }

  // 🎨 COLOR ESTADO
  getColorEstado(estado: any) {
    const nombre = estado?.nombre || '';

    switch (nombre) {
      case 'PENDIENTE': return 'bg-warning';
      case 'EN_COCINA': return 'bg-primary';
      case 'LISTO': return 'bg-success';
      case 'PAGADO': return 'bg-dark';
      case 'CANCELADO': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }

  // 🔒 SOLO ACTIVOS EDITABLES
  puedeEditar(p: any) {
    return p.estado?.nombre !== 'CANCELADO'
        && p.estado?.nombre !== 'PAGADO';
  }

    // 🔹 PAGINACIÓN
  get pedidosPaginados() {
    const inicio = (this.pagina - 1) * this.tamanoPagina;
    const fin = inicio + this.tamanoPagina;
    return this.pedidos.slice(inicio, fin);
  }

  get totalPaginas() {
    return Math.ceil(this.pedidos.length / this.tamanoPagina);
  }
}