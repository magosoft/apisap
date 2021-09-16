/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author GUIDO CACERES PINTO
 */
@Provider
public class GELExceptionMapper implements ExceptionMapper<GELExceptionMapping> {

    @Override
    public Response toResponse(GELExceptionMapping exception) {
        e(exception);
        return exception.getResponse();
    }

    private void e(GELExceptionMapping exception) {
        if (exception != null) {
            String s = exception.toString();
            if (exception.getCause() != null && exception.getCause().getMessage() != null) {
                s += " Causa: " + exception.getCause().getMessage();
                //exception.getCause().printStackTrace();
            }
            Logger.getLogger("API-SAP").log(Level.SEVERE, s);
        }
    }

}
