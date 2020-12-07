package com.lafuente.sap.exceptions;

/**
 *
 * @author GUIDO CACERES PINTO
 */
public class GELException extends Exception {

    private final int status;
    private final String code;

    public GELException() {
        this("Error desconocido o no registrado!");
    }

    public GELException(Throwable cause) {
        this("Error desconocido o no registrado!", cause);
    }

    public GELException(String message) {
        this("NOT_REGISTERED", message);
    }

    public GELException(String message, Throwable cause) {
        this("NOT_REGISTERED", message, cause);
    }

    public GELException(String code, String message) {
        this(409, code, message);
    }

    public GELException(String code, String message, Throwable cause) {
        this(409, code, message, cause);
    }

    public GELException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public GELException(int status, String code, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.code = code;
    }

    /*Aqui es otro*/
    public GELException(CodeError error) {
        this(error.getCodeResponse(), error.name(), error.getMessage());
    }

    public GELException(CodeError error, Throwable cause) {
        this(error.getCodeResponse(), error.name(), error.getMessage(), cause);
    } 

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

}
