import { Component, OnInit } from '@angular/core';
import { CategoriaService, Categoria } from '../../../services/categoria.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-admin-categorias',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-categorias.html'
})
export class AdminCategoriaComponent implements OnInit {

  categorias: Categoria[] = [];
  filtro = '';
  loading = false;
  mensaje ="";

  // 🔹 paginación
  pagina = 1;
  tamanoPagina = 5;

  // 🔹 crear
  nueva: any = {
    nombre: ''
  };

  // 🔹 editar
  editando = false;

  categoriaEdit: any = {
    idCat: null,
    nombre: ''
  };

  constructor(
    private service: CategoriaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.listar();
  }

  // 🔹 PAGINACIÓN
  get categoriasPaginadas() {
    const inicio = (this.pagina - 1) * this.tamanoPagina;
    const fin = inicio + this.tamanoPagina;
    return this.categorias.slice(inicio, fin);
  }

  get totalPaginas() {
    return Math.ceil(this.categorias.length / this.tamanoPagina);
  }

  // 🔹 LISTAR
  listar() {
    this.loading = true;
    this.pagina = 1;

    this.service.listar().subscribe({
      next: data => {
        this.categorias = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudieron cargar las categorías'
        });
        this.cdr.detectChanges();
      }
    });
  }

  // 🔹 BUSCAR (frontend simple)
buscar() {
  this.loading = true;
  this.mensaje = '';
  this.pagina = 1;

  if (!this.filtro) {
    this.listar();
    return;
  }

  this.service.buscar(this.filtro).subscribe({
    next: data => {
      this.categorias = data;
      this.loading = false;

      if (data.length === 0) {
        Swal.fire({
          icon: 'info',
          title: 'Sin resultados',
          text: 'No se encontró la categoría'
        });
      }

      this.cdr.detectChanges();
    },
    error: () => {
      this.loading = false;
      this.mensaje = 'Error en la búsqueda de categoría';
      this.cdr.detectChanges();
    }
  });
}

  // 🔹 CREAR
  crear() {
      if (!this.nueva.nombre) {
        Swal.fire({
          icon: 'warning',
          title: 'Campo requerido',
          text: 'Debe ingresar el nombre de la categoría'
        });
        return;
      }

  this.service.crear(this.nueva).subscribe(() => {
    Swal.fire({
      icon: 'success',
      title: 'Categoría creada',
      text: 'La categoría se registró correctamente',
      confirmButtonColor: '#3085d6'
    });

    this.nueva = { nombre: '' };
    this.listar();
  });
   /* if (!this.nueva.nombre) {
      alert('Ingrese nombre de categoría');
      return;
    }

    this.service.crear(this.nueva).subscribe(() => {
      alert('Categoría creada');

      this.nueva = { nombre: '' };

      this.listar();
    });*/
  }

  // 🔹 ELIMINAR
  eliminar(id: number) {
    Swal.fire({
      title: '¿Eliminar categoría?',
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
            text: 'La categoría fue eliminada correctamente'
          });
          this.listar();
        });
      }
    });
  }

  // 🔹 EDITAR
  editar(c: Categoria) {
    this.editando = true;

    this.categoriaEdit = {
      idCat: c.idCat,
      nombre: c.nombre
    };
  }

  // 🔹 ACTUALIZAR
  actualizar() {

    if (!this.categoriaEdit.idCat) {
      console.error('ID inválido');
      return;
    }

    this.service.actualizar(this.categoriaEdit.idCat, this.categoriaEdit)
      .subscribe(() => {
        Swal.fire({
          icon: 'success',
          title: 'Categoría actualizada',
          text: 'Los cambios se guardaron correctamente',
          confirmButtonColor: '#3085d6'
        });
        this.listar();
        this.cancelarEdicion();
      });
  }

  cancelarEdicion() {
    this.editando = false;

    this.categoriaEdit = {
      idCat: null,
      nombre: ''
    };
  }

  resetear() {
    this.filtro = '';
    this.mensaje = '';
    this.pagina = 1;
    this.listar();
  }

}