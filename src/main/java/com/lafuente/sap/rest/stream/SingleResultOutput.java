
package com.lafuente.sap.rest.stream;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lafuente.sap.utils.SAPUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author GUIDO CACERES PINTO
 */
public class SingleResultOutput implements StreamingOutput {

    private final ResultSet resultSet;

    public SingleResultOutput(ResultSet resultSet) {
        this.resultSet = resultSet;
        
    }

    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
        final ObjectMapper objectMapper = new ObjectMapper();
        try (JsonGenerator jg = objectMapper.getFactory()
                .createGenerator(output, JsonEncoding.UTF8)) {
            writeResultSetToJson(resultSet, jg);
            jg.flush();
        } catch (IOException | SQLException ex) {
            throw new IOException(ex);
        }

    }

    private static void writeResultSetToJson(
            final ResultSet rs,
            final JsonGenerator jg)
            throws SQLException, IOException {
        final ResultSetMetaData rsmd = rs.getMetaData();
        final int columnCount = rsmd.getColumnCount();
        String column;
        //jg.writeStartArray();
        if (rs.next()) {
            jg.writeStartObject();
            for (int i = 1; i <= columnCount; i++) {
                column = rsmd.getColumnLabel(i);
                if (StringUtils.isEmpty(column)) {
                    column = rsmd.getColumnName(i);
                }
                writeObjectField(jg, SAPUtils.camelCase(column), rs.getObject(i));
            }
            jg.writeEndObject();
        } else {            
            throw new SQLException("El objeto no puede ser nulo!");
        }
        if (rs.next()) {
            throw new SQLException("El objeto tiene duplicidad!");
        }
        //jg.writeEndArray();
    }

    private static void writeObjectField(JsonGenerator jsonObject, String column, Object value) throws IOException {
        if (value == null) {
            jsonObject.writeNullField(column);
        } else if (value instanceof Integer) {
            jsonObject.writeNumberField(column, (Integer) value);
        } else if (value instanceof String) {
            jsonObject.writeStringField(column, (String) value);
        } else if (value instanceof Boolean) {
            jsonObject.writeBooleanField(column, (Boolean) value);
        } else if (value instanceof Date) {
            jsonObject.writeStringField(column, SAPUtils.dateToStr((Date) value));
        } else if (value instanceof Long) {
            jsonObject.writeNumberField(column, (Long) value);
        } else if (value instanceof Double) {
            jsonObject.writeNumberField(column, (Double) value);
        } else if (value instanceof Float) {
            jsonObject.writeNumberField(column, (Float) value);
        } else if (value instanceof BigDecimal) {
            jsonObject.writeNumberField(column, (BigDecimal) value);
        } else if (value instanceof Byte) {
            jsonObject.writeNumberField(column, (Byte) value);
        } else if (value instanceof byte[]) {
            jsonObject.writeBinaryField(column, (byte[]) value);
        } else {
            jsonObject.writeObjectField(column, value);
        }
    }
}
