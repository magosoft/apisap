/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest.dto;

import java.math.BigDecimal;

/**
 *
 * @author dcaceres
 */
public class CuotaDTO {
    
    private String numeroCuota;
    private String fecha;
    private BigDecimal valorCuota;
    private BigDecimal saldo;
    private String moneda;

    public String getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(String numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(BigDecimal valorCuota) {
        this.valorCuota = valorCuota;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
    
    

}
