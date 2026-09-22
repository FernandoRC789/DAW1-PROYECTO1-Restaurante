import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import { ProductoService } from '../../../services/producto';
import { Producto } from '../../../models/producto.model';
import { Categoria } from '../../../models/categoria.model'; // 👈 AJUSTA RUTA
import { CategoriaService} from '../../../services/categoria.service';
declare var bootstrap: any; // para usar Bootstrap JS

@Component({
  selector: 'app-productos-list',
  standalone: true,
  templateUrl: './productos-list.html', // asegúrate que el archivo exista
  styleUrls: ['./productos-list.css'],
   imports: [CommonModule, FormsModule]   // <-- añade FormsModule aquí
})
export class ProductosListComponent implements OnInit {
  productos: Producto[] = [];
  categorias: Categoria[] = [];
  // objeto usado en el modal de creación
producto: Producto = {
  idComida: 0,
  nombre: '',
  descripcion: '',
  precioUni: 0,
  disponible: true,
  categoria: {
    idCat: 0,
    nombre: ''
  }
};







    constructor(
    private productoService: ProductoService,
      private categoriaService: CategoriaService,
    private cd: ChangeDetectorRef   // <-- inyecta ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarProductos();
    this.categoriaService.getCategorias().subscribe({
      next: (data: Categoria[]) => this.categorias = data,
      error: (err: any) => console.error('Error al cargar categorías', err)
    });
  }
  cargarProductos(): void {
    this.productoService.getProductos().subscribe({
      next: (data) => {
        console.log('Datos recibidos:', data);
        this.productos = data;
        this.cd.detectChanges();   // <-- fuerza renderizado inmediato
      },
      error: (err) => console.error('Error al cargar productos', err)
    });
  }

  // 👉 Aquí va tu método abrirModal
  abrirModal(): void {
    const modal = new bootstrap.Modal(document.getElementById('crearModal'));
    modal.show();
  }

guardar(): void {

  // ✅ Validar categoría correctamente
  if (!this.producto.categoria || this.producto.categoria.idCat === 0) {
    alert('Debe seleccionar una categoría válida');
    return;
  }

  this.productoService.createProducto(this.producto).subscribe({
    next: () => {
      alert('Comida creada con éxito');
      this.cargarProductos();

      // cerrar modal
      const modal = bootstrap.Modal.getInstance(document.getElementById('crearModal'));
      modal?.hide();

      // ✅ reset limpio
      this.producto = {
        idComida: 0,
        nombre: '',
        descripcion: '',
        precioUni: 0,
        disponible: true,
        categoria: {
          idCat: 0,
          nombre: ''
        }
      };
    },
    error: (err) => {
      console.error('Error al crear comida', err);
      alert('Error al guardar. Verifica categoría.');
    }
  });
}


}

