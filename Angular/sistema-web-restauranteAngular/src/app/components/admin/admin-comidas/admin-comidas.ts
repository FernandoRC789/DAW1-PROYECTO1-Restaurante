import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CategoriaService } from '../../../services/categoria.service';
import { Comida } from '../../../models/comida.model';
import { Categoria } from '../../../models/categoria.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ComidaService } from '../../../services/comida.service';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-admin-comidas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-comidas.html'
})
export class AdminComidasComponent implements OnInit {

  loading = false;

  filtroDisponible: string = '';
  comidas: Comida[] = [];
  categorias: Categoria[] = [];

  filtro = '';
  categoriaFiltro = '';

  pagina = 1;
  tamanoPagina = 4;

  editando = false;
  mensaje="";

  allComidas: Comida[] = [];

  nuevo: Comida = {
    idComida: null as any,
    nombre: '',
    descripcion: '', // 🔥 AGREGAR
    precioUni: 0.0,
    disponible: true,
    categoria: null as any
  };

  comidaEdit: Comida = {
    idComida: null as any,
    nombre: '',
    descripcion: '', // 🔥 AGREGAR
    precioUni: 0.0,
    disponible: true,
    categoria: null as any
  };

  constructor(
    private service: ComidaService,
    private categoriaService: CategoriaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.pagina =1;
    this.listar();
    this.cargarCategorias();
  }

  // 🔹 PAGINACIÓN
  get comidasPaginadas() {
    const inicio = (this.pagina - 1) * this.tamanoPagina;
    const fin = inicio + this.tamanoPagina;
    return this.comidas.slice(inicio, fin);
  }

  get totalPaginas() {
    return Math.ceil(this.comidas.length / this.tamanoPagina);
  }

  listar() {
    this.loading = true;
    this.pagina = 1;
    this.mensaje="";

    this.service.listar().subscribe({
      next: data => {
        this.comidas = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudieron cargar las comidas'
        });
        this.cdr.detectChanges();
      }
    });
    this.filtro="";

  }

  resetear() {
    this.filtro = '';
    this.categoriaFiltro = '';
    this.filtroDisponible = '';
    this.mensaje = '';
    this.pagina = 1;

    this.listar(); // 🔥 recién aquí recargas
  }

  cargarCategorias() {
    this.categoriaService.getCategorias().subscribe(data => {
      this.categorias = data;
    });
  }

  /*buscar() {
    this.service.buscar(this.filtro).subscribe(data => {
      this.comidas = data;
      this.pagina = 1;
      this.cdr.detectChanges(); // 🔥 CLAVE
    });
  }*/

  buscar() {
    this.aplicarFiltros();
    /*this.loading = true;
    this.categoriaFiltro = '';
    this.mensaje="";

    this.service.buscar(this.filtro).subscribe({
      next: data => {
        this.comidas = data;
        this.pagina = 1;
        this.loading = false;

        if (data.length === 0) {
          this.mensaje = 'Comida no encontrada, revise el nombre e intente nuevamente';
        }

        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.mensaje = 'Error en la búsqueda';
        this.cdr.detectChanges();
      }
    });*/
  }
  filtrarCategoria() {
    this.aplicarFiltros();
    /*this.pagina = 1;
    this.mensaje = '';
    this.loading = true;
    this.filtro="";

    if (!this.categoriaFiltro) {
      this.listar();
      return;
    }

    this.service.porCategoria(this.categoriaFiltro)
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: data => {
          this.comidas = data;

          if (data.length === 0) {
            this.mensaje = 'No hay platos en esta categoría';
          }
        },
        error: () => {
          this.mensaje = 'Error al filtrar';
        }
      });*/
  }

  crear() {
    if (!this.nuevo.nombre || !this.nuevo.categoria) {
      Swal.fire({
        icon: 'warning',
        title: 'Campos incompletos',
        text: 'Debes completar todos los campos obligatorios'
      });
      return;
    }

    if (!this.nuevo.descripcion) {
      Swal.fire({
        icon: 'warning',
        title: 'Descripción requerida',
        text: 'La descripción es obligatoria'
      });
      return;
    }

    this.service.crear(this.nuevo).subscribe(() => {
        Swal.fire({
          icon: 'success',
          title: 'Comida Creada',
          text: 'El plato de comida se registro correctamente',
          confirmButtonColor: '#3085d6'
        });
        this.listar();

      this.nuevo = {
        idComida: null as any,
        nombre: '',
        descripcion: '', // 🔥 AGREGAR
        precioUni: 0.0,
        disponible: true,
        categoria: null as any
      };



    });
  }

  editar(c: Comida) {
    this.editando = true;
    this.comidaEdit = { ...c };
  }

  actualizar() {
    const payload = {
      ...this.comidaEdit,
      categoria: {
        idCat: this.comidaEdit.categoria.idCat
      }
    };

    this.service.actualizar(this.comidaEdit.idComida, payload)
      .subscribe(() => {
        Swal.fire({
          icon: 'success',
          title: 'Comida actualizada',
          text: 'El plato se actualizó correctamente',
          confirmButtonColor: '#3085d6'
        });
        this.listar();
        this.cancelar();
      });
  }

  eliminar(id: number) {
    Swal.fire({
      title: '¿Eliminar comida?',
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
            text: 'El plato fue eliminado correctamente'
          });
          this.listar();
        });
      }
    });
  }

  cambiarDisponible(id: number, estado: boolean) {
  this.service.cambiarDisponible(id, estado).subscribe({
    next: () => {

      const comida = this.comidas.find(c => c.idComida === id);
      if (comida) {
        comida.disponible = estado;
      }

      // 🔥 también en la lista completa
      const comidaAll = this.allComidas?.find(c => c.idComida === id);
      if (comidaAll) {
        comidaAll.disponible = estado;
      }

      this.cdr.detectChanges();

      Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'success',
        title: 'Disponibilidad actualizada',
        showConfirmButton: false,
        timer: 1500
      });

    },
    error: () => {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo actualizar disponibilidad'
      });
    }
  });
}
  /*cambiarDisponible(id: number, estado: boolean) {
    this.service.cambiarDisponible(id, estado).subscribe(() => {
      this.listar();
    });
  }*/

  cancelar() {
    this.editando = false;
  }

  getColorDisponible(disponible: boolean) {
    return disponible ? 'bg-success' : 'bg-danger';
  }

  filtrarDisponibles() {
    this.aplicarFiltros();
  /*this.pagina = 1;

  if (this.filtroDisponible === '') {
    this.listar();
    return;
  }

  if (this.filtroDisponible === 'true') {
    this.service.disponibles().subscribe(data => {
      this.comidas = data;
    });
  }

  if (this.filtroDisponible === 'false') {
    this.service.noDisponibles().subscribe(data => {
      this.comidas = data;
    });
  }*/
}

aplicarFiltros() {
  this.loading = true;
  this.pagina = 1;
  this.mensaje = '';

  this.service.listar().subscribe({
    next: data => {

      let resultado = data;

      // 🔍 FILTRO POR NOMBRE
      if (this.filtro) {
        resultado = resultado.filter(c =>
          c.nombre.toLowerCase().includes(this.filtro.toLowerCase())
        );
      }

      // 🏷️ FILTRO POR CATEGORIA
      if (this.categoriaFiltro) {
        resultado = resultado.filter(c =>
          c.categoria?.idCat == this.categoriaFiltro
        );
      }

      // ✅ FILTRO POR DISPONIBLE
      if (this.filtroDisponible !== '') {
        const estado = this.filtroDisponible === 'true';
        resultado = resultado.filter(c =>
          c.disponible === estado
        );
      }

      this.comidas = resultado;

      if (resultado.length === 0) {
        Swal.fire({
          icon: 'info',
          title: 'Sin resultados',
          text: 'No se encontraron comidas con esos filtros'
        });
      }

      this.loading = false;
      this.cdr.detectChanges();
    },
    error: () => {
      this.loading = false;
      this.mensaje = 'Error al aplicar filtros';
      this.cdr.detectChanges();
    }
  });
}
}