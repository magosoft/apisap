package com.lafuente.sap.exceptions;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author GUIDO CACERES PINTO
 */
@Provider
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    private static final boolean DEFAULT_SHOW_DETAILS = true;
    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;
    private final boolean showDetails;
    private final MediaType mediaType;

    public JsonProcessingExceptionMapper() {
        this(DEFAULT_SHOW_DETAILS, DEFAULT_MEDIA_TYPE);
    }

    public JsonProcessingExceptionMapper(boolean showDetails, MediaType mediaType) {
        this.showDetails = showDetails;
        this.mediaType = mediaType;
    }

    @Override
    public Response toResponse(JsonProcessingException exception) {
        final String message = exception.getOriginalMessage();
        CodeError error = CodeError.GEL50051;
        if (exception instanceof JsonGenerationException) {
            error = CodeError.GEL50052;
        }
        if (exception instanceof JsonParseException) {
            error = CodeError.GEL50053;
        }
        if (message.startsWith("No suitable constructor found")) {
            error = CodeError.GEL50054;
        }
        if (message.startsWith("No serializer found for class")) {
            error = CodeError.GEL50054;
        }
        if (message.startsWith("Can not construct instance")) {
            error = CodeError.GEL50054;
        }
        if (showDetails) {
            Logger.getLogger("API-SAP")
                    .log(Level.SEVERE, message);
        }
        return Response.status(error.getCodeResponse())
                .type(mediaType)
                .entity(new ResponseError(error.name(), error.getMessage()))
                .build();
    }

}
