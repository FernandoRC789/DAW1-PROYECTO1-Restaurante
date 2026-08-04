import { Component } from '@angular/core';
import { ProductoService } from '../../../services/producto';
import { Producto } from '../../../models/producto.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-productos-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './productos-form.html',
  styleUrls: ['./productos-form.css']
})
export class ProductosFormComponent {
producto: Producto = {
  idComida: 0,
  nombre: '',
  descripcion: '',
  precioUni: 0,
  disponible: true,
  categoria: {
    idCat: 0,
    nombre: ''
  }};



  constructor(private productoService: ProductoService) {}

  guardar(): void {
    this.productoService.createProducto(this.producto).subscribe({
      next: (data) => {
        console.log('Producto creado', data);
        alert('Producto guardado con éxito');
      },
      error: (err) => console.error('Error al guardar producto', err)
    });
  }
}


