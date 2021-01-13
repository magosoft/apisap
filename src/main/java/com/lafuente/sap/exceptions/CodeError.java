package com.lafuente.sap.exceptions;

/**
 *
 * @author GUIDO CACERES PINTO
 */
public enum CodeError {
    GEL20000(500, "El sistema no puede completar la acción, problemas con la base de datos."),
    GEL20001(400, "No puede realizar un INSERT, valores nulos!"),
    GEL20002(400, "No puede realizar un UPDATE, valores nulos!"),
    
    GEL40100(401, "El usuario no ha iniciado sesión. ¡Se requiere autenticación!"),
    
    GEL40000(409, "Error desconocido."),
    GEL50000(500, "Upps, tuvimos un error (también tenemos bugs)."),
    GEL50051(400, "No se puede procesar el json."),
    GEL50052(500, "Error al generar el json."),
    GEL50053(500, "Error al analizar el json."),
    GEL50054(500, "No se puede deserializar el tipo específico del json."),
    GEL30001(404, "No tiene cuotas, para realizar un cobro."),
    GEL30002(404, "El número de cuota de la reserva, no es válida."),
    GEL30003(404, "El tipo de documento no es una RESERVA."),
    GEL30004(404, "El monto ingresado excede el saldo de la reserva."),
    GEL30012(404, "El número de cuota del contrato, no es válida."),
    GEL30013(404, "El tipo de documento no es un CONTRATO."),
    GEL30014(404, "Existe inconsistencia en el importe de la cuota."),
    GEL30015(404, "El tipo de documento, es incorrecta."),
    GEL30016(404, "La sumatoria de la cuotas no es igual al total."),
    GEL30017(404, "Las cuotas no puede estar vacia."),
    GEL30018(404, "No puede ser nulo PagoDTO."),
    GEL30019(404, "La seqcuo no son identicas."),
    GEL30020(404, "Usuario o Banco no asignado."),
    GEL30021(404, "El sistema no puede completar la acción, problemas con el servicio SAP."),
    GEL30023(404, "El sistema no puede completar la acción, codigo comercio sin asignar."),
    GEL30024(409, "El importe no puede estar vacio!"),
    GEL30025(409, "El numeroCuota no puede estar vacio!"),
    GEL30026(409, "Intente de nuevo con una cuota inicial menor!"),
    GEL30050(503, "El sistema no puede completar la acción, problemas con el servicio web de TIGOMONEY."),
    GEL30060(503, "El sistema no puede completar la acción, problemas con el servicio web de LINKSER."),
    GEL30070(503, "El sistema no puede completar la acción, problemas con el servicio web de SAP."),
    GEL30080(503, "El sistema no puede completar la acción, problemas con el servicio QR."),
    
    ;

    private final String message;
    private final int codeResponse;

    private CodeError(int codeResponse, String message) {
        this.message = message;
        this.codeResponse = codeResponse;
    }

    public String getMessage() {
        return message;
    }

    public int getCodeResponse() {
        return codeResponse;
    }
}
