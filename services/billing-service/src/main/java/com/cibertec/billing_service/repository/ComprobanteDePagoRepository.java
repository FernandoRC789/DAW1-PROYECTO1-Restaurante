package com.cibertec.billing_service.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.cibertec.billing_service.model.ComprobanteDePago;
import com.cibertec.billing_service.utilsEnum.MetodoPago;
import com.cibertec.billing_service.utilsEnum.TipoComprobante;

/**
 * 📌 REPOSITORIO: ComprobantePagoRepository
 * 
 * 🔥 RESPONSABILIDAD:
 * Manejar acceso a datos de comprobantes de pago
 * 
 * 🔥 SOPORTA:
 * - Generación de comprobantes
 * - Validaciones
 * - Reportes
 * - Historial de ventas
 */
public interface ComprobanteDePagoRepository extends JpaRepository<ComprobanteDePago, Long>{


    // =========================
    // 🔢 GENERACIÓN DE NÚMERO
    // =========================

    /**
     * 🔍 Obtener el último comprobante por tipo
     * 
     * 🔥 Usado para generar correlativo tipo SUNAT
     * Ejemplo:
     * B001-000001 → B001-000002
     */
    ComprobanteDePago findTopByTipoComprobanteOrderByIdComprobantePagoDesc(TipoComprobante tipo);

    /**
     * 🔍 Obtener el último comprobante registrado en general
     */
    Optional<ComprobanteDePago> findTopByOrderByIdComprobantePagoDesc();


    // =========================
    // 🔥 VALIDACIONES
    // =========================

    /**
     * ❌ Verificar si un pedido ya tiene comprobante
     */
    boolean existsByOrderId(Long orderId);

    /**
     * ❌ Verificar si el número ya existe (seguridad)
     */
    boolean existsByNumero(String numero);


    // =========================
    // 🔍 BÚSQUEDAS
    // =========================

    /**
     * 🔍 Buscar por número de comprobante
     */
    Optional<ComprobanteDePago> findByNumero(String numero);

    /**
     * 🔍 Buscar comprobantes por cliente
     */
    List<ComprobanteDePago> findByCliente(Long cliente);

    /**
     * 🔍 Buscar comprobantes por cajero
     */
    List<ComprobanteDePago> findByUserID(Long cajero);

    /**
     * 🔍 Buscar por tipo (BOLETA / FACTURA)
     */
    List<ComprobanteDePago> findByTipoComprobante(TipoComprobante tipo);

    /**
     * 🔍 Buscar por método de pago
     */
    List<ComprobanteDePago> findByMetodoPago(MetodoPago metodoPago);


    // =========================
    // 📊 REPORTES
    // =========================

    /**
     * 📅 Buscar comprobantes por rango de fechas
     */
    List<ComprobanteDePago> findByFechaEmisionBetween(
            LocalDateTime inicio,
            LocalDateTime fin
    );

    /**
     * 📊 Reporte por fecha + tipo
     */
    List<ComprobanteDePago> findByFechaEmisionBetweenAndTipoComprobante(
            LocalDateTime inicio,
            LocalDateTime fin,
            TipoComprobante tipo
    );

    /**
     * 📊 Reporte por fecha + método de pago
     */
    List<ComprobanteDePago> findByFechaEmisionBetweenAndMetodoPago(
            LocalDateTime inicio,
            LocalDateTime fin,
            MetodoPago metodoPago
    );
    

    @Query("""
        SELECT c FROM ComprobanteDePago c
        WHERE DATE(c.fechaEmision) = :fecha
        AND c.estado.nombre = 'PAGADO'
    """)
    List<ComprobanteDePago> findByFecha(@Param("fecha") LocalDate fecha);
}