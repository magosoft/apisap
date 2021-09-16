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
public class ProyectoDTO {
    private String bukrs;
    private String urlImgApp;
    private BigDecimal min;
    private String maktx;
    private String bukrsCom;
    private String matnr;
    private BigDecimal montoReseva;
    private String lanzamiento;
    private String urlImg;
    private String waerk;
    private String saleType;

    public String getBukrs() {
        return bukrs;
    }

    public void setBukrs(String bukrs) {
        this.bukrs = bukrs;
    }

    public String getUrlImgApp() {
        return urlImgApp;
    }

    public void setUrlImgApp(String urlImgApp) {
        this.urlImgApp = urlImgApp;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public String getMaktx() {
        return maktx;
    }

    public void setMaktx(String maktx) {
        this.maktx = maktx;
    }

    public String getBukrsCom() {
        return bukrsCom;
    }

    public void setBukrsCom(String bukrsCom) {
        this.bukrsCom = bukrsCom;
    }

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public BigDecimal getMontoReseva() {
        return montoReseva;
    }

    public void setMontoReseva(BigDecimal montoReseva) {
        this.montoReseva = montoReseva;
    }

    public String getLanzamiento() {
        return lanzamiento;
    }

    public void setLanzamiento(String lanzamiento) {
        this.lanzamiento = lanzamiento;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String getWaerk() {
        return waerk;
    }

    public void setWaerk(String waerk) {
        this.waerk = waerk;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

}
