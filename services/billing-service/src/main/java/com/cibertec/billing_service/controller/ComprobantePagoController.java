package com.cibertec.billing_service.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.cibertec.billing_service.dto.CierreCajaDTO;
import com.cibertec.billing_service.dto.ComprobanteDTO;
import com.cibertec.billing_service.dto.ComprobanteResponseDTO;
import com.cibertec.billing_service.service.ComprobantePagoService;
import com.cibertec.billing_service.utilsEnum.MetodoPago;

/**
 * CONTROLADOR: ComprobantePagoController
 * 
 * Expone endpoints REST para gestionar comprobantes de pago.
 * Usado por cajero.
 */
@RestController
@RequestMapping("/api/comprobantes")
public class ComprobantePagoController {

    @Autowired
    private ComprobantePagoService comprobanteService;

    /**
     * Listar comprobantes
     */
    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(comprobanteService.listar());
    }

    /**
     * Buscar por número
     */
    @GetMapping("/numero/{numero}")
    public ResponseEntity<?> buscarPorNumero(@PathVariable String numero) {
        return ResponseEntity.ok(comprobanteService.buscarPorNumero(numero));
    }

    /**
     * Buscar por cliente
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> buscarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(comprobanteService.buscarPorCliente(idCliente));
    }

    /**
     * Buscar por método de pago
     */
    @GetMapping("/metodo/{metodo}")
    public ResponseEntity<?> buscarPorMetodo(@PathVariable MetodoPago metodo) {
        return ResponseEntity.ok(comprobanteService.buscarPorMetodo(metodo));
    }

    /**
     * Anular comprobante de pago
     */
    @PatchMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable Long id) {
        return ResponseEntity.ok(comprobanteService.anular(id));
    }

    /**
     * Generar comprobante
     */
    @PostMapping("/generar")
    public ResponseEntity<ComprobanteResponseDTO> generar(@Valid @RequestBody ComprobanteDTO dto) {
        // 1. Guardamos el comprobante y obtenemos la entidad de BD
        var comprobanteGuardado = comprobanteService.crearDesdeDTO(dto);
        
        // 2. Retornamos el DTO enriquecido con cliente y detalles usando su ID
        ComprobanteResponseDTO responseDTO = comprobanteService.obtenerComprobanteConCliente(comprobanteGuardado.getIdComprobantePago());
        
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Cierre de caja por fecha
     */
    @GetMapping("/cierre")
    public ResponseEntity<CierreCajaDTO> cierreCaja(@RequestParam String fecha) {
        LocalDate fechaParseada = LocalDate.parse(fecha);
        return ResponseEntity.ok(comprobanteService.cierrePorFecha(fechaParseada));
    }

    /**
     * Obtener detalle con cliente
     */
    @GetMapping("/{id}/detalle-cliente")
    public ResponseEntity<ComprobanteResponseDTO> obtenerDetalleConCliente(@PathVariable Long id) {
        return ResponseEntity.ok(comprobanteService.obtenerComprobanteConCliente(id));
    }
}
