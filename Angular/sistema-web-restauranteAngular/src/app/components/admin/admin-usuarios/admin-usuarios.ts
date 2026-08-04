import { Component, OnInit } from '@angular/core';
import { UsuarioService, Usuario } from '../../../services/usuario.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-usuarios.html'
})
export class AdminUsuariosComponent implements OnInit {

  rolesSeleccionados: string[] = [];
  usuarios: Usuario[] = [];
  filtro = '';
  rolFiltro = '';
  totalRol = 0;
  mensaje="";

  loading = false;

  rolesNuevo = {
    ADMIN: false,
    MESERO: false,
    COCINA: false,
    CAJERO: false
  };

  rolesEdit = {
    ADMIN: false,
    MESERO: false,
    COCINA: false,
    CAJERO: false
  };

  nuevo: any = {
    username: '',
    password: '',
    roles: []
  };

  //para paginacion - nick
  pagina = 1;
  tamanoPagina = 4;

  editando: boolean = false;

  usuarioEdit: any = {
  idUser: null,
  username: '',
  password: '',
  roles: []
  };

  constructor(
    private service: UsuarioService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.listar();
  }

  get usuariosPaginados() {
  const inicio = (this.pagina - 1) * this.tamanoPagina;
  const fin = inicio + this.tamanoPagina;
  return this.usuarios.slice(inicio, fin);
  }
  
  get totalPaginas() {
  return Math.ceil(this.usuarios.length / this.tamanoPagina);
  }

  getRolesSeleccionadosNuevo(): string[] {
    const lista: string[] = [];

    if (this.rolesNuevo.ADMIN) lista.push('ADMIN');
    if (this.rolesNuevo.MESERO) lista.push('MESERO');
    if (this.rolesNuevo.COCINA) lista.push('COCINA');
    if (this.rolesNuevo.CAJERO) lista.push('CAJERO');

    return lista;
  }

  getRolesSeleccionadosEdit(): string[] {
  const lista: string[] = [];

  if (this.rolesEdit.ADMIN) lista.push('ADMIN');
  if (this.rolesEdit.MESERO) lista.push('MESERO');
  if (this.rolesEdit.COCINA) lista.push('COCINA');
  if (this.rolesEdit.CAJERO) lista.push('CAJERO');

  return lista;
}

  listar() {
    this.loading = true;
    this.pagina = 1;
    this.rolFiltro = '';
    this.totalRol = 0;

    this.service.listar().subscribe({
      next: data => {
        this.usuarios = data;
        this.loading = false;
        this.cdr.detectChanges(); // 🔥 FIX
      },
      error: () => {
        this.loading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudieron cargar los usuarios'
        });
        this.cdr.detectChanges();
      }
    });
  }

    resetear() {
    this.filtro = '';
    this.rolFiltro = '';
    this.mensaje = '';
    this.pagina = 1;

    this.listar(); // 🔥 recién aquí recargas
  }

  buscar() {
    this.loading=true;
    this.mensaje="";

    this.service.buscar(this.filtro).subscribe({
      next: data => {
        this.usuarios = data;
        this.pagina = 1;
        this.loading=false;

      if (data.length === 0) {
        Swal.fire({
          icon: 'info',
          title: 'Sin resultados',
          text: 'No se encontró el usuario'
        });
      }

        this.cdr.detectChanges(); // 🔥 CLAVE
      },
      error: () => {
        this.loading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Error en la búsqueda de usuario'
        });
        this.cdr.detectChanges();
      }
    });
  }

  filtrarRol() {
    this.pagina = 1;
    if (!this.rolFiltro) {
      this.listar();
      return;
    }

    this.usuarios = [];
    this.totalRol = 0;
    this.loading = true;

    forkJoin({
      usuarios: this.service.porRol(this.rolFiltro),
      total: this.service.contarPorRol(this.rolFiltro)
    })
    .pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.detectChanges(); // 🔥 FIX IMPORTANTE
      }))
    .subscribe({
      next: (resp) => {
        this.usuarios = resp.usuarios;
        this.totalRol = resp.total;
        this.cdr.detectChanges(); // 🔥 FIX CLAVE
      }
    });

  }

crear() {

  const roles = this.getRolesSeleccionadosNuevo();

  if (roles.length === 0) {
    Swal.fire({
      icon: 'warning',
      title: 'Roles requeridos',
      text: 'Debe seleccionar al menos un rol'
    });
    return;
  }

  const payload: any = {
    username: this.nuevo.username,
    password: this.nuevo.password,
    roles: roles
  };

  this.service.crear(payload).subscribe({
    next: () => {
      Swal.fire({
        icon: 'success',
        title: 'Usuario creado',
        text: 'El usuario se registró correctamente',
        confirmButtonColor: '#3085d6'
      });
      this.nuevo = {
        username: '',
        password: ''
      };

      this.rolesNuevo = {
        ADMIN: false,
        MESERO: false,
        COCINA: false,
        CAJERO: false
      };

      this.listar(); // 🔥 refresca tabla
    }
  });
}


  eliminar(id: number) {
    Swal.fire({
      title: '¿Eliminar usuario?',
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
            text: 'El usuario fue eliminado correctamente'
          });
          this.pagina = 1;
          this.listar();
        });
      }
    });
  }

  actualizar() {

    if (!this.usuarioEdit.idUser) {
      console.error('No hay usuario seleccionado');
      return;
    }

    const roles = this.getRolesSeleccionadosEdit();

    const payload = {
      username: this.usuarioEdit.username,
      password: this.usuarioEdit.password,
      roles: roles
    };

    this.service.actualizar(this.usuarioEdit.idUser, payload)
      .subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Usuario actualizado',
            text: 'Los cambios se guardaron correctamente',
            confirmButtonColor: '#3085d6'
          });
          this.listar();
          this.cancelarEdicion();
        }
      });
  }

  cancelarEdicion() {
    this.editando = false;

    this.usuarioEdit = {
      idUser: null,
      username: '',
      password: '',
      roles: []
    };

    this.rolesEdit = {
      ADMIN: false,
      MESERO: false,
      COCINA: false,
      CAJERO: false
    };
  }

  editar(u: Usuario) {
  this.editando = true;

  this.usuarioEdit = {
    idUser: u.idUser,
    username: u.username,
    password: '', // ⚠️ por seguridad normalmente NO se carga
    roles: u.roles.map(r => r.nombre)
  };

  this.rolesEdit = {
    ADMIN: u.roles.some(r => r.nombre === 'ADMIN'),
    MESERO: u.roles.some(r => r.nombre === 'MESERO'),
    COCINA: u.roles.some(r => r.nombre === 'COCINA'),
    CAJERO: u.roles.some(r => r.nombre === 'CAJERO')
  };
}
}