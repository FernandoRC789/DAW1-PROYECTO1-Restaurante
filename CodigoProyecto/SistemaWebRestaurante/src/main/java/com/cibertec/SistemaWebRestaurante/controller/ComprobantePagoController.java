package com.cibertec.SistemaWebRestaurante.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.SistemaWebRestaurante.dto.CierreCajaDTO;
import com.cibertec.SistemaWebRestaurante.dto.ComprobanteDTO;
import com.cibertec.SistemaWebRestaurante.dto.ComprobanteResponseDTO;
import com.cibertec.SistemaWebRestaurante.model.ComprobanteDePago;
import com.cibertec.SistemaWebRestaurante.service.ComprobantePagoService;
import com.cibertec.SistemaWebRestaurante.utilsEnum.MetodoPago;

/**
 * 📌 CONTROLADOR: ComprobantePagoController
 * 
 * 🔥 Usado por cajero
 */
@RestController
@RequestMapping("/api/comprobantes")
@CrossOrigin("*")	
public class ComprobantePagoController {

    @Autowired
    private ComprobantePagoService comprobanteService;

    /**
     * 📌 Crear comprobante
     
    @PostMapping("/nuevo")
    public ResponseEntity<?> crear(@RequestBody ComprobanteDTO dto) {
        return ResponseEntity.ok(comprobanteService.crearDesdeDTO(dto));
    }*/

    /**
     * 📌 Listar comprobantes
     */
    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(comprobanteService.listar());
    }

    /**
     * 📌 Buscar por número
     */
    @GetMapping("/numero/{numero}")
    public ResponseEntity<?> buscarPorNumero(@PathVariable String numero) {
        return ResponseEntity.ok(comprobanteService.buscarPorNumero(numero));
    }
    
    //buscar por clientes
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> buscarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(comprobanteService.buscarPorCliente(idCliente));
    }
    
    //buscar por metodo de pago
    @GetMapping("/metodo/{metodo}")
    public ResponseEntity<?> buscarPorMetodo(@PathVariable MetodoPago metodo) {
        return ResponseEntity.ok(comprobanteService.buscarPorMetodo(metodo));
    }
    
    //anular comprobante de pago
    @PatchMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable Long id) {
        return ResponseEntity.ok(comprobanteService.anular(id));
    }

    /**
     * 💰 Generar comprobante
     */
    @PostMapping("/generar")
    public ResponseEntity<ComprobanteResponseDTO> generar(@RequestBody ComprobanteDTO dto) {

        ComprobanteDePago comp = comprobanteService.crearDesdeDTO(dto);

        return ResponseEntity.ok(comprobanteService.convertir(comp));
    }
    /*@PostMapping("/generar")
    public ResponseEntity<ComprobanteDePago> generar(
            @RequestBody ComprobanteDTO dto
    ) {
        return ResponseEntity.ok(
            comprobanteService.crearComprobante(
                dto.getPedidoId(),
                dto.getClienteId(),
                dto.getTipoComprobante(),
                dto.getMetodoPago()
            )
        );
    }*/
    
    @GetMapping("/cierre")
    public ResponseEntity<CierreCajaDTO> cierreCaja(
            @RequestParam String fecha
    ) {

        LocalDate fechaParseada = LocalDate.parse(fecha);

        return ResponseEntity.ok(
                comprobanteService.cierrePorFecha(fechaParseada)
        );
    }
    
}
