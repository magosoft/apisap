/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 *
 * @author dcaceres
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoteDTO {
    private int proyecto;
    private String uv;
    private String mz;
    private String lt;
    private String lote;
    private BigDecimal superficie;
    private BigDecimal ventaM2;
    private String categoria;
    private BigDecimal precioLista;
    private String moneda;
    private BigDecimal descuento;
    private BigDecimal totalPp;
    private BigDecimal montoCuota;
    private BigDecimal nuevoPrecioVenta;
    private BigDecimal precioListaAjuste;
    private int maximoDia;
    private BigDecimal nuevoTotalPp;
    private int maximoPlazo;
    private BigDecimal cuotaInicial;
    private BigDecimal porcentaje;

    public int getProyecto() {
        return proyecto;
    }

    public void setProyecto(int proyecto) {
        this.proyecto = proyecto;
    }

    public String getUv() {
        return uv;
    }

    public void setUv(String uv) {
        this.uv = uv;
    }

    public String getMz() {
        return mz;
    }

    public void setMz(String mz) {
        this.mz = mz;
    }

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public BigDecimal getSuperficie() {
        return superficie;
    }

    public void setSuperficie(BigDecimal superficie) {
        this.superficie = superficie;
    }

    public BigDecimal getVentaM2() {
        return ventaM2;
    }

    public void setVentaM2(BigDecimal ventaM2) {
        this.ventaM2 = ventaM2;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecioLista() {
        return precioLista;
    }

    public void setPrecioLista(BigDecimal precioLista) {
        this.precioLista = precioLista;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getTotalPp() {
        return totalPp;
    }

    public void setTotalPp(BigDecimal totalPp) {
        this.totalPp = totalPp;
    }

    public BigDecimal getMontoCuota() {
        return montoCuota;
    }

    public void setMontoCuota(BigDecimal montoCuota) {
        this.montoCuota = montoCuota;
    }

    public BigDecimal getNuevoPrecioVenta() {
        return nuevoPrecioVenta;
    }

    public void setNuevoPrecioVenta(BigDecimal nuevoPrecioVenta) {
        this.nuevoPrecioVenta = nuevoPrecioVenta;
    }

    public BigDecimal getPrecioListaAjuste() {
        return precioListaAjuste;
    }

    public void setPrecioListaAjuste(BigDecimal precioListaAjuste) {
        this.precioListaAjuste = precioListaAjuste;
    }

    public int getMaximoDia() {
        return maximoDia;
    }

    public void setMaximoDia(int maximoDia) {
        this.maximoDia = maximoDia;
    }

    public BigDecimal getNuevoTotalPp() {
        return nuevoTotalPp;
    }

    public void setNuevoTotalPp(BigDecimal nuevoTotalPp) {
        this.nuevoTotalPp = nuevoTotalPp;
    }

    public int getMaximoPlazo() {
        return maximoPlazo;
    }

    public void setMaximoPlazo(int maximoPlazo) {
        this.maximoPlazo = maximoPlazo;
    }

    public BigDecimal getCuotaInicial() {
        return cuotaInicial;
    }

    public void setCuotaInicial(BigDecimal cuotaInicial) {
        this.cuotaInicial = cuotaInicial;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }
    
}
