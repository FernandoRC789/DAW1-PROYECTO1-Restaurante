import { Component, OnInit } from '@angular/core';
import { MesaService } from '../../../services/mesa.service';
import { Mesa } from '../../../models/mesa.model';
import { EstadoMesa } from '../../../models/estado-mesa.enum';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
//para recargar la lista de las mesas
import { ChangeDetectorRef } from '@angular/core';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-admin-mesas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-mesas.html'
})
export class AdminMesasComponent implements OnInit {

  loading = false;
  mesas: Mesa[] = [];

  pagina = 1;
  tamanoPagina = 4;

  nuevo: Mesa = {
    idMesa: null as any,
    numero: '',
    asientos: 1,
    estado_mesa: EstadoMesa.LIBRE
  };


  editando = false;

  mesaEdit: Mesa = {
    idMesa: null as any,
    numero: '',
    asientos: 1,
    estado_mesa: EstadoMesa.LIBRE
  };

  constructor(
    private service: MesaService,
    private cdr: ChangeDetectorRef
  ) {}
    // 🔹 PAGINACIÓN


  get mesasPaginadas() {
    const inicio = (this.pagina - 1) * this.tamanoPagina;
    const fin = inicio + this.tamanoPagina;
    return this.mesas.slice(inicio, fin);
  }

  get totalPaginas() {
    return Math.ceil(this.mesas.length / this.tamanoPagina);
  }

  ngOnInit() {
    this.pagina = 1;
    this.listar();
  }

  listar() {
    this.loading = true;
    this.pagina = 1;

    this.service.listar().subscribe({
      next: data => {
        this.mesas = data;
        this.loading = false;
        this.cdr.detectChanges(); // 🔥 CLAVE
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges(); // 🔥 TAMBIÉN
      }
    });
  }

    crear() {
      const existe = this.mesas.some(m => m.numero === this.nuevo.numero);
if (existe) {
  Swal.fire({
    icon: 'error',
    title: 'Número duplicado',
    text: 'Ya existe una mesa con ese número'
  });
  return;
}
if (!this.nuevo.numero || this.nuevo.asientos <= 0) {
  Swal.fire({
    icon: 'warning',
    title: 'Datos inválidos',
    text: 'Número y asientos válidos son obligatorios'
  });
  return;
}

    this.service.crear(this.nuevo).subscribe(() => {
        Swal.fire({
          icon: 'success',
          title: 'Mesa Creada',
          text: 'La Mesa se registro correctamente',
          confirmButtonColor: '#3085d6'
        });
        this.listar();

        this.nuevo = {
        idMesa: null as any,
        numero: '',
        asientos: 1,
        estado_mesa: EstadoMesa.LIBRE
        };
    });
    }

  editar(m: Mesa) {
    this.editando = true;
    this.mesaEdit = { ...m };
  }

  actualizar() {
    this.service.actualizar(this.mesaEdit.idMesa, this.mesaEdit)
      .subscribe(() => {
        Swal.fire({
          icon: 'success',
          title: 'Mesa actualizada',
          text: 'Los datos se guardaron correctamente',
          confirmButtonColor: '#3085d6'
        });
        this.listar();
        this.cancelar();
      });
  }

  eliminar(id: number) {
    const mesa = this.mesas.find(m => m.idMesa === id);

if (mesa?.estado_mesa === EstadoMesa.OCUPADO) {
  Swal.fire({
    icon: 'warning',
    title: 'No permitido',
    text: 'No puedes eliminar una mesa ocupada'
  });
  return;
}

    Swal.fire({
      title: '¿Eliminar mesa?',
      text: 'Esta acción no se puede deshacer',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.eliminar(id).subscribe(() => {
          Swal.fire({
            icon: 'success',
            title: 'Eliminado',
            text: 'La mesa fue eliminada correctamente'
          });
          this.listar();
        });
      }
    });
  }

  /*cambiarEstado(id: number, estado: EstadoMesa) {
    this.service.cambiarEstado(id, estado).subscribe(() => {
      const mesa = this.mesas.find(m => m.idMesa === id);
      if (mesa) mesa.estado_mesa = estado;
    });
  }*/
 cambiarEstado(id: number, estado: EstadoMesa) {
  this.service.cambiarEstado(id, estado).subscribe({
    next: () => {

      const mesa = this.mesas.find(m => m.idMesa === id);
      if (mesa) mesa.estado_mesa = estado;

      Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'success',
        title: 'Estado actualizado',
        showConfirmButton: false,
        timer: 1500
      });
        this.cdr.detectChanges(); // 🔥 TAMBIÉN

    },
    error: () => {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo cambiar el estado'
      });
    }
  });
}

  confirmarCambioEstado(mesa: any, nuevoEstado: EstadoMesa) {

  if (mesa.estado_mesa === nuevoEstado) return;

  Swal.fire({
    title: '¿Cambiar estado?',
    text: `Mesa ${mesa.numero}: ${mesa.estado_mesa} → ${nuevoEstado}`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, cambiar',
    cancelButtonText: 'Cancelar'
  }).then(result => {

    if (result.isConfirmed) {
      this.cambiarEstado(mesa.idMesa, nuevoEstado);
    }

  });
}

    cancelar() {
    this.editando = false;
    this.mesaEdit = {
        idMesa: null as any,
        numero: '',
        asientos: 1,
        estado_mesa: EstadoMesa.LIBRE
    };
    }

    getColorEstado(estado: EstadoMesa) {
    switch (estado) {
        case EstadoMesa.LIBRE: return 'bg-success';
        case EstadoMesa.OCUPADO: return 'bg-danger';
        case EstadoMesa.RESERVADO: return 'bg-warning';
        case EstadoMesa.NO_DISPONIBLE: return 'bg-secondary';
        default: return 'bg-dark';
    }
    }
}