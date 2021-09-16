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
public class PlazoDTO {

    private String delco;
    private int plazo;
    private BigDecimal porcentaje;
    private String tipoPago;

    public String getDelco() {
        return delco;
    }

    public void setDelco(String delco) {
        this.delco = delco;
    }

    public int getPlazo() {
        return plazo;
    }

    public void setPlazo(int plazo) {
        this.plazo = plazo;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

}
