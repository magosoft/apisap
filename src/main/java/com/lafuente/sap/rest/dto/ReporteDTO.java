/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;

/**
 *
 * @author dcaceres
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteDTO {

    private String nombre;
    private HashMap<String, String> queryParams;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public HashMap<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(HashMap<String, String> queryParams) {
        this.queryParams = queryParams;
    }

}
