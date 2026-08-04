import { Component, OnInit } from '@angular/core';
import { MesaService } from '../../../services/mesa.service';
import { ComidaService } from '../../../services/comida.service';
import { PedidoService } from '../../../services/pedido.service';

import { Mesa } from '../../../models/mesa.model';
import { Comida } from '../../../models/comida.model';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PedidoDTO } from '../../../models/pedido.model';
import { EstadoMesa } from '../../../models/estado-mesa.enum';
import { ChangeDetectorRef } from '@angular/core';
import { finalize } from 'rxjs/operators';
import Swal from 'sweetalert2';

interface ItemPedido {
  comida: Comida;
  cantidad: number;
}

@Component({
  selector: 'app-pedido-builder',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mesero-crearPedido.html'
})
export class PedidoBuilderComponent implements OnInit {

  mesas: Mesa[] = [];
  comidas: Comida[] = [];
  mesaSeleccionada: Mesa | null = null;
    EstadoMesa = EstadoMesa; // 👈 AQUÍ VA 🔥


  carrito: ItemPedido[] = [];

  loading = false;
  observaciones = '';

    abrirModal = false;
filtro = '';

  constructor(
    private mesaService: MesaService,
    private comidaService: ComidaService,
    private pedidoService: PedidoService,
    private cdr: ChangeDetectorRef // 👈 AGREGAR
  ) {}

  ngOnInit() {
    this.cargarMesas();
    this.cargarComidas();
  }

filtroDisponible = '';

comidasFiltradas() {
  return this.comidas.filter(c => {

    const nombreOk = c.nombre.toLowerCase()
      .includes(this.filtro.toLowerCase());

    const estadoOk =
      this.filtroDisponible === '' ||
      c.disponible === (this.filtroDisponible === 'true');

    return nombreOk && estadoOk;
  });
}
cargarMesas() {
  this.loading = true;

  this.mesaService.listar()
    .pipe(finalize(() => {
      this.loading = false;
      this.cdr.detectChanges();
    }))
    .subscribe(data => {
      this.mesas = data;
    });
}

  cargarComidas() {
    this.comidaService.listar().subscribe(data => {
      console.log('COMIDAS:', data);
      this.comidas = data;
      this.cdr.detectChanges(); // 🔥 CLAVE
    });
  }

  // 🛒 AGREGAR AL CARRITO
  agregar(comida: Comida) {
    const item = this.carrito.find(i => i.comida.idComida === comida.idComida);

    if (item) {
      item.cantidad++;
    } else {
      this.carrito.push({ comida, cantidad: 1 });
    }
      this.filtro = ''; // limpia búsqueda
  }


  // ➖ RESTAR
  restar(item: ItemPedido) {
    item.cantidad--;

    if (item.cantidad <= 0) {
      this.carrito = this.carrito.filter(i => i !== item);
    }
  }


  // ❌ ELIMINAR
  eliminar(item: ItemPedido) {
    //this.carrito = this.carrito.filter(i => i !== item);
    Swal.fire({
      toast: true,
      position: 'top-end',
      icon: 'info',
      title: 'Eliminado del carrito',
      showConfirmButton: false,
      timer: 1000
    });
  }

  // 💰 TOTAL
  get total() {
    return this.carrito.reduce((sum, item) =>
      sum + (item.comida.precioUni * item.cantidad), 0);
  }

  // 🧾 CREAR PEDIDO
crearPedido() {


  if (!this.mesaSeleccionada) {
    Swal.fire({
      icon: 'warning',
      title: 'Mesa requerida',
      text: 'Selecciona una mesa'
    });
    return;
  }

  if (this.carrito.length === 0) {
    Swal.fire({
      icon: 'warning',
      title: 'Carrito vacío',
      text: 'Agrega al menos una comida'
    });
    return;
  }

      Swal.fire({
      title: 'Creando pedido...',
      allowOutsideClick: false,
      didOpen: () => {
        Swal.showLoading();
      }
    });
    const dto: PedidoDTO = {
    estadoId: 1,
    mesaId: this.mesaSeleccionada!.idMesa,
    observaciones: this.observaciones, // 🔥 usa tu variable

    detalle: this.carrito.map(i => ({
        comidaId: i.comida.idComida,
        cantidad: i.cantidad
    }))
    };

    this.pedidoService.crear(dto)
      .pipe(finalize(() => this.cdr.detectChanges()))
      .subscribe({
        next: () => {
          Swal.close();
        Swal.fire({
          icon: 'success',
          title: 'Pedido creado',
          text: 'Se registró correctamente el pedido',
          confirmButtonColor: '#3085d6'
        });
          // 🔥 REFRESCAR MESAS (CLAVE)
          this.cargarMesas();
          this.resetFormulario();
        },
        error: () => {
          Swal.close();
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'No se pudo crear el pedido'
          });
        }
      });
}

  // 🎨 COLOR MESA
  getColorEstado(estado: string) {
    switch (estado) {
      case 'LIBRE': return 'bg-success';
      case 'OCUPADO': return 'bg-danger';
      case 'RESERVADO': return 'bg-warning';
      default: return 'bg-secondary';
    }
  }

actualizarCantidad(item: ItemPedido) {
  if (!item.cantidad || item.cantidad < 1) {
    item.cantidad = 1;
  }
}

resetFormulario() {
  this.carrito = [];
  this.mesaSeleccionada = null;
  this.observaciones = '';
  this.filtro = '';
  this.abrirModal = false;
  this.loading = false;

  this.cdr.detectChanges(); // 🔥 clave
}
}