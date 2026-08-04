package com.cibertec.SistemaWebRestaurante.dto;

import java.math.BigDecimal;

public class CierreCajaDTO {

    private BigDecimal totalGeneral;
    private BigDecimal totalEfectivo;
    private BigDecimal totalTarjeta;
    private BigDecimal totalYape;
    private int cantidadComprobantes;
	public BigDecimal getTotalGeneral() {
		return totalGeneral;
	}
	public void setTotalGeneral(BigDecimal totalGeneral) {
		this.totalGeneral = totalGeneral;
	}
	public BigDecimal getTotalEfectivo() {
		return totalEfectivo;
	}
	public void setTotalEfectivo(BigDecimal totalEfectivo) {
		this.totalEfectivo = totalEfectivo;
	}
	public BigDecimal getTotalTarjeta() {
		return totalTarjeta;
	}
	public void setTotalTarjeta(BigDecimal totalTarjeta) {
		this.totalTarjeta = totalTarjeta;
	}
	public BigDecimal getTotalYape() {
		return totalYape;
	}
	public void setTotalYape(BigDecimal totalYape) {
		this.totalYape = totalYape;
	}
	public int getCantidadComprobantes() {
		return cantidadComprobantes;
	}
	public void setCantidadComprobantes(int cantidadComprobantes) {
		this.cantidadComprobantes = cantidadComprobantes;
	}
    
    
}
