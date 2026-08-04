import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CajaService } from '../../../services/cajero.service';
import { PedidoService } from '../../../services/pedido.service';

import { Subject, catchError, interval, switchMap, takeUntil } from 'rxjs';
import { ClienteService } from '../../../services/cliente.service';
import { ComprobanteRequest } from '../../../models/comprobante.request.model';
import { finalize } from 'rxjs';

import { startWith } from 'rxjs';
import { of } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { ComprobanteResponse } from '../../../models/comprobante.model';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-cajero-generar-comprobante',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cajero-generar-comprobante.html'
})
export class CajeroGenerarComprobanteComponent implements OnInit, OnDestroy {

  // ==========================
  // 📦 LISTA DE PEDIDOS LISTOS
  // ==========================
  pedidos: any[] = [];
private pollingActivo = false;
  // 🎯 pedido seleccionado en caja
  pedidoSeleccionado: any = null;

  // 💳 datos de pago
  metodoPago: string = 'EFECTIVO';
  tipoComprobante: string = 'BOLETA';

  // 🔄 control de carga
  loading = false;

  // 👤 cliente
  cliente: any = {
    idCliente: null,
    nombre: '',
    documento: '',
    tipoDocumento: 'DNI',
    direccion: '',
    telefono: '',
    correo: ''
  };
loadingPedidos = true;
  // 🧹 evita memory leaks
  private destroy$ = new Subject<void>();

  constructor(
    private cajaService: CajaService,
    private pedidoService: PedidoService,
    private clienteService: ClienteService,
      private cdr: ChangeDetectorRef

  ) {}

  ngOnInit() {
    // 🔥 carga inicial + polling (TIEMPO REAL)
    //this.cargarPedidosListos();
    this.iniciarPolling();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
      this.pollingActivo = false; // 🔥 importante

  }

  // ==================================
  // 🔄 POLLING: ACTUALIZA AUTOMÁTICO
  // ==================================
  iniciarPolling() {
      if (this.pollingActivo) return; // 🛑 evita duplicados
  this.pollingActivo = true;

    interval(4000)
      .pipe(
        startWith(0), // 🔥 DISPARA INMEDIATAMENTE
        takeUntil(this.destroy$),
        switchMap(() =>
          this.pedidoService.listar()
            .pipe(
            catchError(() => of([]))
            )
        )
      )
      .subscribe(data => {
          this.loadingPedidos = false;

        // SOLO pedidos listos (cajero trabaja solo con esto)
        this.pedidos = data.filter(p =>
          this.normalizarEstado(p.estado?.nombre) === 'LISTO'
        );

        // 🔥 si el pedido seleccionado ya no existe, se limpia
        if (this.pedidoSeleccionado) {
          const existe = this.pedidos.find(
            p => p.idPedido === this.pedidoSeleccionado.idPedido
          );

          if (!existe) {
            this.pedidoSeleccionado = null;

            Swal.fire({
              icon: 'info',
              title: 'Pedido actualizado',
              text: 'El pedido seleccionado ya no está disponible'
            });
          }
        }

          this.cdr.detectChanges(); // 🔥 ESTE ES EL CLAVE

      });
  }

  // ==================================
  // 📦 CARGA INICIAL
  // ==================================
  cargarPedidosListos() {
    this.pedidoService.listar()
      .subscribe(data => {
      this.pedidos = data.filter(p =>
        this.normalizarEstado(p.estado?.nombre) === 'LISTO'
      );
      });
  }

  // 🔎 buscar cliente por documento
buscarCliente() {
  if (!this.cliente.documento) return;

  this.clienteService.buscarPorDocumento(this.cliente.documento)
    .subscribe({
      next: (data) => {

        // ✔ merge en lugar de reemplazo total
    this.cliente = {
      idCliente: data.idCliente ?? null,
      nombre: data.nombre ?? this.cliente.nombre,
      documento: data.documento ?? this.cliente.documento,
      tipoDocumento: data.tipoDocumento ?? this.cliente.tipoDocumento,
      direccion: data.direccion ?? '',
      telefono: data.telefono ?? '',
      correo: data.correo ?? ''
    };
      },
      error: () => {
        this.cliente.idCliente = null;
      }
    });
}

normalizarEstado(e: string) {
  return (e || '').trim().toUpperCase();
}

  seleccionarPedido(p: any) {
    this.pedidoSeleccionado = p;
  }

  // ==================================
  // 💰 GENERAR COMPROBANTE (POS REAL)
  // ==================================
    // 💰 generar comprobante
generar() {

  if (this.loading) return;

  if (!this.pedidoSeleccionado) {
    Swal.fire({
      icon: 'warning',
      title: 'Pedido requerido',
      text: 'Debes seleccionar un pedido'
    });
    return;
  }

  if (!this.validarCliente(this.cliente)) {
    Swal.fire({
      icon: 'warning',
      title: 'Cliente inválido',
      text: 'Verifica los datos del cliente antes de continuar'
    });
    return;
  }

  this.loading = true;
    Swal.fire({
    title: 'Procesando pago...',
    text: 'Por favor espera',
    allowOutsideClick: false,
    didOpen: () => {
      Swal.showLoading();
    }
  });

  const cliente$ = this.cliente.idCliente
    ? this.clienteService.actualizar(this.cliente)
    : this.clienteService.crear(this.cliente);

  cliente$
    .pipe(
      switchMap(cliente => {

        const request: ComprobanteRequest = {
          pedidoId: this.pedidoSeleccionado.idPedido,
          clienteId: cliente.idCliente,
          tipoComprobante: this.tipoComprobante as any,
          metodoPago: this.metodoPago as any
        };

        console.log("REQUEST:", request);

        return this.cajaService.generar(request);
      }),

          finalize(() => {
      this.loading = false; // 🔥 SIEMPRE se ejecuta
    })
    )
    .subscribe({
      next: (comprobante) => {
        console.log("COMPROBANTE:", comprobante);
        this.generarPDF(comprobante); // 🔥 AQUI

        // 🔥 limpiar UI correctamente
        this.pedidos = this.pedidos.filter(
          p => p.idPedido !== this.pedidoSeleccionado.idPedido
        );

        this.pedidoSeleccionado = null;
        this.resetCliente();

        Swal.close();

        Swal.fire({
          icon: 'success',
          title: 'Pago realizado',
          text: 'El comprobante se generó correctamente',
          confirmButtonColor: '#3085d6'
        });
          this.cdr.detectChanges(); // 🔥 ESTE TE FALTABA
      },

      error: (err) => {
        console.error("ERROR COMPLETO:", err);
        Swal.fire({
          icon: 'error',
          title: 'Error en el pago',
          text: err?.error?.message || 'Ocurrió un problema al generar el comprobante'
        });
      }
    });
}

  // ==========================
  // 💰 TOTAL DEL PEDIDO
  // ==========================
  getTotal(pedido: any): number {
    return pedido?.total || 0;
  }

validarCliente(cliente: any): boolean {

  if (this.esFactura()) {
    // FACTURA → solo RUC
    return cliente.documento?.length === 11 && cliente.nombre;
  }

  if (this.esBoleta()) {
    // BOLETA → DNI o CE
    return (
      (cliente.documento?.length === 8 || cliente.documento?.length === 12) &&
      cliente.nombre
    );
  }

  return false;
}

esBoleta(): boolean {
  return this.tipoComprobante === 'BOLETA';
}

esFactura(): boolean {
  return this.tipoComprobante === 'FACTURA';
}

resetCliente() {
  this.cliente = {
    idCliente: null,
    nombre: '',
    documento: '',
    tipoDocumento: 'DNI',
    direccion: '',
    telefono: '',
    correo: ''
  };
}

generarPDF(c: ComprobanteResponse) {

  const doc = new jsPDF();

  // =========================
  // 🏪 DATOS DEL NEGOCIO
  // =========================
  doc.setFontSize(14);
  doc.setFont("helvetica", "bold");
  doc.text("RESTAURANTE SABOR PERUANO", 105, 10, { align: "center" });

  doc.setFontSize(9);
  doc.setFont("helvetica", "normal");
  doc.text("RUC: 12345678901", 105, 16, { align: "center" });
  doc.text("Av. Principal 123 - Lima", 105, 20, { align: "center" });
  doc.text("Tel: 987654321 | correo@restaurante.com", 105, 24, { align: "center" });

  // Línea separadora
  doc.line(14, 28, 196, 28);

  // =========================
  // 🧾 DATOS DEL COMPROBANTE
  // =========================
  doc.setFontSize(11);
  doc.setFont("helvetica", "bold");
  doc.text(`${c.tipoComprobante}`, 14, 36);

  doc.setFont("helvetica", "normal");
  doc.setFontSize(10);
  doc.text(`N°: ${c.numero}`, 14, 42);
  doc.text(`Fecha: ${this.formatearFecha(c.fechaEmision)}`, 14, 48);
  doc.text(`Método: ${c.metodoPago}`, 14, 54);

  // =========================
  // 👤 CLIENTE
  // =========================
  doc.line(14, 58, 196, 58);

  doc.setFont("helvetica", "bold");
  doc.text("DATOS DEL CLIENTE", 14, 64);

  doc.setFont("helvetica", "normal");
  doc.text(`Nombre: ${c.clienteNombre || 'Cliente general'}`, 14, 70);
  doc.text(`Documento: ${c.clienteDocumento || '-'}`, 14, 76);

  // =========================
  // 🍔 DETALLE DE PRODUCTOS
  // =========================
  autoTable(doc, {
    startY: 82,
    head: [['Producto', 'Cant', 'Precio', 'Total']],
    body: c.detalles.map(d => [
      d.producto,
      d.cantidad,
      `S/ ${d.precio.toFixed(2)}`,
      `S/ ${(d.cantidad * d.precio).toFixed(2)}`
    ]),
    styles: {
      fontSize: 9
    },
    headStyles: {
      fillColor: [0, 0, 0]
    }
  });

  const finalY = (doc as any).lastAutoTable.finalY || 100;

  // =========================
  // 💰 TOTALES
  // =========================
  const subtotal = c.total / 1.18;
  const igv = c.total - subtotal;

  doc.setFontSize(10);

  doc.text(`Subtotal: S/ ${subtotal.toFixed(2)}`, 140, finalY + 10);
  doc.text(`IGV (18%): S/ ${igv.toFixed(2)}`, 140, finalY + 16);

  doc.setFont("helvetica", "bold");
  doc.text(`TOTAL: S/ ${c.total.toFixed(2)}`, 140, finalY + 24);

  // =========================
  // 🙏 MENSAJE FINAL
  // =========================
  doc.setFont("helvetica", "normal");
  doc.setFontSize(9);

  doc.text( "Cada plato, una experiencia. ¡Gracias por elegirnos!", 105, finalY + 40, { align: "center" });
  doc.text("Vuelva pronto", 105, finalY + 46, { align: "center" });

  doc.text("Ante cualquier duda o reclamo, comuníquese con nosotros presentando esta boleta/factura", 105, finalY + 80, { align: "center" });

 

  // =========================
  // 💾 DESCARGA
  // =========================
  doc.save(`comprobante_${c.numero}.pdf`);
}

formatearFecha(fecha: string): string {
  return new Date(fecha).toLocaleString();
}
}