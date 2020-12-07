package com.lafuente.sap.exceptions;

import java.io.Serializable;

/**
 *
 * @author GUIDO CACERES PINTO
 */
public class ResponseError implements Serializable {

    private String codigo;
    private String mensaje;

    public ResponseError() {
        this("ERROR DESCONOCIDO!!");
    }

    public ResponseError(String mensaje) {
        this("NO REGISTRADO", mensaje);
    }

    public ResponseError(String codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje == null ? "ERROR DESCONOCIDO!!" : mensaje;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "{ " + codigo + " }{ " + mensaje + " }";
    }

}
