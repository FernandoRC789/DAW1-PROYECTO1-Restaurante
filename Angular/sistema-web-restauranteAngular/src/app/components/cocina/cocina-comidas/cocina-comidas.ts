import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComidaService } from '../../../services/comida.service';
import { FormsModule } from '@angular/forms';
import { Subject, interval, switchMap, takeUntil } from 'rxjs';
import Swal from 'sweetalert2';



@Component({
  selector: 'app-cocina-comidas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cocina-comidas.html'
})
export class CocinaComidasComponent implements OnInit {

  // 🧾 lista mostrada en pantalla
  comidas: any[] = [];

  // 📦 lista completa desde backend
  allComidas: any[] = [];

  // 🎯 filtro de disponibilidad
  filtroDisponibilidad: string = 'TODOS';

  textoBusqueda: string = '';

loadingIds: Set<number> = new Set();
  paginaActual: number = 1;
tamanoPagina: number = 5;
  // 🧹 control de destrucción (evita memory leaks)
  private destroy$ = new Subject<void>();

  constructor(
    private comidaService: ComidaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    // 🔥 carga inicial inmediata
    this.cargarComidas();

    // 🔄 actualizaciones en tiempo real (polling)
    this.iniciarPolling();
  }

  ngOnDestroy() {
    // 🧹 limpia observable cuando sales del componente
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ============================
  // 🔄 POLLING (TIEMPO REAL)
  // ============================
iniciarPolling() {
  interval(8000) // más lento (menos choque)
    .pipe(
      takeUntil(this.destroy$),
      switchMap(() => this.comidaService.listar())
    )
    .subscribe(data => {

      // ⚠️ solo actualizar si NO estás editando
      if (this.loadingIds.size === 0) {
        this.allComidas = data;
        this.aplicarFiltro();
      }
    });
}

  // ============================
  // 📦 CARGA INICIAL
  // ============================
  cargarComidas() {
    this.comidaService.listar()
      .subscribe({
        next: (data) => {
          this.allComidas = data;
          this.aplicarFiltro();
          this.cdr.detectChanges();
        },
        error: () => {
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'No se pudieron cargar las comidas'
          });
        }
      });
  }

  // ============================
  // 🎯 FILTRO DE DISPONIBILIDAD
  // ============================
aplicarFiltro() {
  let lista = this.allComidas;

  // 🎯 FILTRO POR DISPONIBILIDAD
  if (this.filtroDisponibilidad === 'DISPONIBLE') {
    lista = lista.filter(c => c.disponible === true);
  }

  if (this.filtroDisponibilidad === 'NO_DISPONIBLE') {
    lista = lista.filter(c => c.disponible === false);
  }

  // 🔎 FILTRO POR TEXTO
  const texto = this.textoBusqueda.trim().toLowerCase();

  if (texto) {
    lista = lista.filter(c =>
      c.nombre.toLowerCase().includes(texto)
    );
  }

  this.comidas = lista;
}

  // ============================
  // 🔄 CAMBIO DE DISPONIBILIDAD
  // ============================
cambiarEstadoComida(comida: any) {

  Swal.fire({
  title: '¿Cambiar disponibilidad?',
  text: comida.disponible ? 'Se marcará como NO disponible' : 'Se marcará como disponible',
  icon: 'question',
  showCancelButton: true, 
  confirmButtonText: 'Sí, cambiar',
  cancelButtonText: 'Cancelar'
}).then(result => {
  if (!result.isConfirmed) return;

  // 👉 aquí va TODO tu código actual
    const id = comida.idComida;

  if (this.loadingIds.has(id)) return;

  this.loadingIds.add(id);

  const updated = {
    ...comida,
    disponible: !comida.disponible
  };

  // ⚡ UI instantánea SIN romper referencia global
  const index = this.allComidas.findIndex(c => c.idComida === id);
  if (index !== -1) {
    this.allComidas[index] = updated;
  }

  this.aplicarFiltro();

  this.comidaService.actualizar(id, updated)
    .subscribe({
      next: (data) => {
        Swal.fire({
          toast: true,
          position: 'top-end',
          icon: 'success',
          title: 'Estado actualizado',
          showConfirmButton: false,
          timer: 1500
        });
        const i = this.allComidas.findIndex(c => c.idComida === id);
        if (i !== -1) {
          this.allComidas[i] = data;
        }

        this.aplicarFiltro();

          this.cdr.detectChanges();

      },

      error: () => {
        // rollback
        const i = this.allComidas.findIndex(c => c.idComida === id);
        if (i !== -1) {
          this.allComidas[i].disponible = comida.disponible;
        }

        this.aplicarFiltro();
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo actualizar el estado de la comida'
        });
      },

      complete: () => {
        this.loadingIds.delete(id);
      }
    });
});
}

get comidasPaginadas() {

  const inicio = (this.paginaActual - 1) * this.tamanoPagina;
  const fin = inicio + this.tamanoPagina;

  return this.comidas.slice(inicio, fin);
}

cambiarPagina(nuevaPagina: number) {
  this.paginaActual = nuevaPagina;
}

limpiarBusqueda() {
  this.textoBusqueda = '';
  this.aplicarFiltro();
}
}