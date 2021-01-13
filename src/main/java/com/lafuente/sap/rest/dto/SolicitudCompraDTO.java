/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest.dto;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author dcaceres
 */
public class SolicitudCompraDTO implements Serializable {

    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private String telefono;
    private String numeroDocumento;
    private String extensionDocumento;
    private HashMap<String, Object> proyecto;
    private HashMap<String, Object> lote;
    private HashMap<String, Object> plazo;
    private SimulacionDTO data;

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getExtensionDocumento() {
        return extensionDocumento;
    }

    public void setExtensionDocumento(String extensionDocumento) {
        this.extensionDocumento = extensionDocumento;
    }

    public HashMap<String, Object> getProyecto() {
        return proyecto;
    }

    public void setProyecto(HashMap<String, Object> proyecto) {
        this.proyecto = proyecto;
    }

    public HashMap<String, Object> getLote() {
        return lote;
    }

    public void setLote(HashMap<String, Object> lote) {
        this.lote = lote;
    }

    public HashMap<String, Object> getPlazo() {
        return plazo;
    }

    public void setPlazo(HashMap<String, Object> plazo) {
        this.plazo = plazo;
    }

    public SimulacionDTO getData() {
        return data;
    }

    public void setData(SimulacionDTO data) {
        this.data = data;
    }

}
