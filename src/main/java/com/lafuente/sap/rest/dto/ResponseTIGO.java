package com.lafuente.sap.rest.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 *
 * @author GUIDO CACERES PINTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseTIGO {

    private int codRes;
    private String mensaje;
    private String orderId;
    private String transaccion;

    public ResponseTIGO() {
        codRes = -1;
    }

    public ResponseTIGO(Map<String, String> map) {
        codRes = Integer.parseInt(map.get("codRes"));
        mensaje = map.get("mensaje");
        orderId = map.get("orderId");
        transaccion = map.get("transaccion");
    }

    public int getCodRes() {
        return codRes;
    }

    public void setCodRes(int codRes) {
        this.codRes = codRes;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(String transaccion) {
        this.transaccion = transaccion;
    }

    @Override
    public String toString() {
        return "{" + "codRes=" + codRes + ", mensaje=" + mensaje + ", orderId=" + orderId + ", transaccion=" + transaccion + '}';
    }

}
