package com.lafuente.sap.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GUIDO CACERES PINTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagoDTO implements Serializable {
    
    private String bukrs;
    private String docven;
    private String matnr;
    private String linea;
    private BigDecimal monto;
    private String moneda;
    private String sucursal;
    private String agencia;
    private ClienteDTO cliente;
    private List<ItemDTO> items;

    public PagoDTO() {
        bukrs = "0000";
        docven = "0000000000";
        matnr = "000000000000000000";
        linea = "0";
        monto = BigDecimal.ZERO;
        moneda = "BOB";
        items = new ArrayList<>();
    }

    public String getBukrs() {
        return bukrs;
    }

    public void setBukrs(String bukrs) {
        this.bukrs = bukrs;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getDocven() {
        return docven;
    }

    public void setDocven(String docven) {
        this.docven = docven;
    }

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public List<ItemDTO> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }
//PAGO CONTRATO#4130001021 PROYECTO#130 CUOTA#10 IMP:10BS

    public String toOrderId() {
        String order = docven + '-';
        if (items.isEmpty()) {
            order += "000";
        } else {
            int size = items.size();
            order += items.get(0).getSeqcuo();
            if (size > 1) {
                order += '-' + items.get(size - 1).getSeqcuo();
            }
        }
        return order;
    }

    public String toMensaje() {
        return "MAT: " + matnr.substring(matnr.length() - 3) + " IMP: " + moneda + " " + monto + " CANT. CUOTAS: " + items.size();
    }

    @Override
    public String toString() {
        return "PagoDTO{" + "bukrs=" + bukrs + ", docven=" + docven + ", matnr=" + matnr + ", linea=" + linea + ", monto=" + monto + ", moneda=" + moneda + ", sucursal=" + sucursal + ", agencia=" + agencia + ", items=" + items + '}';
    }

  

}
