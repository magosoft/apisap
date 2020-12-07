package com.lafuente.sap.exceptions;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author GUIDO CACERES PINTO
 */
@ApplicationException
public class GELExceptionMapping extends RuntimeException {

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;
    private final Response response;

    public GELExceptionMapping(GELException ex) {
        super(ex.getMessage(), ex.getCause());
        response = Response.status(ex.getStatus())
                .type(DEFAULT_MEDIA_TYPE)
                .entity(new ResponseError(ex.getCode(), ex.getMessage()))
                .build();
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        String message = response.readEntity(ResponseError.class).toString();
        return (message != null) ? (s + ": " + message) : s;
    }

}
