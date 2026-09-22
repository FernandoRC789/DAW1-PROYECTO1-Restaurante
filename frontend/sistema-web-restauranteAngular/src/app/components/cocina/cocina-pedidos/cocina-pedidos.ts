import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoService } from '../../../services/pedido.service';
import { takeUntil , Subject, interval, switchMap } from 'rxjs';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-cocina-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cocina-pedidos.html'
})
export class CocinaPedidosComponent implements OnInit {
  // 🧾 Lista que se muestra en pantalla (filtrada)
  pedidos: any[] = [];

  loading = false;

  private alertaMostrada = false;

  // 🎯 filtro del dropdown
  filtroEstado: string = 'TODOS';

  // 📦 Lista completa desde backend (sin filtros)
  allPedidos: any[] = [];

    private destroy$ = new Subject<void>();


  constructor(
    private pedidoService: PedidoService,
      private cdr: ChangeDetectorRef

  ) {}

ngOnInit() {
  this.cargarPedidos();   // 🔥 SOLO UNA CARGA

  this.iniciarPolling();  // luego polling
}

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }


  // 🔄 POLLING CORREGIDO (sin fugas de memoria)
  iniciarPolling() {
    interval(5000)
      .pipe(
        takeUntil(this.destroy$),
        switchMap(() => this.pedidoService.listar())
      )
      .subscribe({
        next: (data) => {
          this.allPedidos = data;
          this.aplicarFiltro();
          this.cdr.detectChanges();
        },
        error: () => {
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Error actualizando pedidos en tiempo real'
          });
        }
      });
  }

aplicarFiltro() {

  const estado = this.filtroEstado;

  if (estado === 'TODOS') {
    this.pedidos = this.allPedidos.filter(p => {
      const est = this.normalizarEstado(p.estado?.nombre);
      return est !== 'LISTO' && est !== 'PAGADO' && est !== 'ANULADO';
    });

          this.alertaMostrada = false; // 🔥 reset

    return;
  }


  this.pedidos = this.allPedidos.filter(p =>
    this.normalizarEstado(p.estado?.nombre) === estado
  );

  // 🔥 SOLO mostrar una vez
  if (this.pedidos.length === 0 && !this.alertaMostrada) {
    this.alertaMostrada = true;

    Swal.fire({
      icon: 'info',
      title: 'Sin pedidos',
      text: 'No hay pedidos en este estado'
    });
  }

  // 🔥 si vuelve a haber pedidos → permitir alerta otra vez
  if (this.pedidos.length > 0) {
    this.alertaMostrada = false;
  }
}

  // 📦 CARGA MANUAL
cargarPedidos() {
this.pedidoService.listar()
  .subscribe({
    next: (data) => {
      this.allPedidos = data;
      this.aplicarFiltro();
      this.cdr.detectChanges();
    },
    error: () => {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudieron cargar los pedidos'
      });
    }
  });
}

  cambiarEstado(id: number, estadoId: number) {
    this.pedidoService.cambiarEstado(id, estadoId)
      .subscribe(() => {
        // refresco inmediato
        //this.iniciarPolling();
              this.cargarPedidos(); // ✅ solo refresca una vez

      });
  }

  // ==============================
  // 🧠 NORMALIZA TEXTO DE ESTADO
  // ==============================
  normalizarEstado(estado: string): string {
  return (estado || '').trim().toUpperCase();
}

  getColor(estado: string) {
    switch (estado) {
      case 'PENDIENTE': return 'bg-warning';
      case 'EN_COCINA': return 'bg-primary';
      case 'LISTO': return 'bg-success';
      default: return 'bg-secondary';
    }
  }


  // 🔥 CAMBIO DE ESTADO (CORRECTO)
  cambiarEstadoPedido(idPedido: number, estadoId: number) {
this.pedidoService.cambiarEstado(idPedido, estadoId)
  .subscribe({
    next: (pedidoActualizado) => {

      const index = this.pedidos.findIndex(p => p.idPedido === idPedido);

      if (index !== -1) {
        this.pedidos[index] = pedidoActualizado;
      }

      this.cdr.detectChanges();

      Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'success',
        title: 'Estado actualizado',
        showConfirmButton: false,
        timer: 1500
      });
    },
    error: () => {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo actualizar el estado del pedido'
      });
    }
  });
  }

}