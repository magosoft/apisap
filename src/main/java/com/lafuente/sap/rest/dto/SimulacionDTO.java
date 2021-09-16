/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author dcaceres
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimulacionDTO {

    private BigDecimal precioLista;
    private BigDecimal precioListaAjustado;
    private BigDecimal totalPlanPago;
    private BigDecimal descuento;
    private BigDecimal precioVenta;
    private BigDecimal precioDescuento;
    private List<CuotaDTO> cuotas;

    public BigDecimal getPrecioLista() {
        return precioLista;
    }

    public void setPrecioLista(BigDecimal precioLista) {
        this.precioLista = precioLista;
    }

    public BigDecimal getPrecioListaAjustado() {
        return precioListaAjustado;
    }

    public void setPrecioListaAjustado(BigDecimal precioListaAjustado) {
        this.precioListaAjustado = precioListaAjustado;
    }

    public BigDecimal getTotalPlanPago() {
        return totalPlanPago;
    }

    public void setTotalPlanPago(BigDecimal totalPlanPago) {
        this.totalPlanPago = totalPlanPago;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getPrecioDescuento() {
        return precioDescuento;
    }

    public void setPrecioDescuento(BigDecimal precioDescuento) {
        this.precioDescuento = precioDescuento;
    }

    public List<CuotaDTO> getCuotas() {
        return cuotas;
    }

    public void setCuotas(List<CuotaDTO> cuotas) {
        this.cuotas = cuotas;
    }

}
