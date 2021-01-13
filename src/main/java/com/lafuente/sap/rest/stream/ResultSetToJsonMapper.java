/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest.stream;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.utils.SAPUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author dcaceres
 */
public class ResultSetToJsonMapper {

    public static byte[] listResultSet(ResultSet resultSet) throws GELException {
        return listResultSet(resultSet, new HashMap<>());
    }

    public static byte[] listResultSet(ResultSet resultSet, HashMap<Integer, String> columnNames) throws GELException {
        byte[] result;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            final ObjectMapper objectMapper = new ObjectMapper();
            try (JsonGenerator jg = objectMapper.getFactory()
                    .createGenerator(output, JsonEncoding.UTF8)) {
                writeListResultSetToJson(resultSet, jg, columnNames);
                jg.flush();
            } catch (IOException | SQLException ex) {
                throw new GELException(ex);
            }
            result = output.toByteArray();
        } catch (IOException ex) {
            throw new GELException(ex);
        }
        return result;
    }

    public static byte[] singleResultSet(ResultSet resultSet) throws GELException {
        return singleResultSet(resultSet, new HashMap<>());
    }

    public static byte[] singleResultSet(ResultSet resultSet, HashMap<Integer, String> columnNames) throws GELException {
        byte[] result;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            final ObjectMapper objectMapper = new ObjectMapper();
            try (JsonGenerator jg = objectMapper.getFactory()
                    .createGenerator(output, JsonEncoding.UTF8)) {
                writeSingleResultSetToJson(resultSet, jg, columnNames);
                jg.flush();
            } catch (IOException | SQLException ex) {
                throw new GELException(ex);
            }
            result = output.toByteArray();
        } catch (IOException ex) {
            throw new GELException(ex);
        }
        return result;
    }

    private static void writeSingleResultSetToJson(ResultSet rs, JsonGenerator jg, HashMap<Integer, String> columnNames) throws SQLException, IOException {
        String[] columns = getColumns(rs, columnNames);
        if (rs.next()) {
            jg.writeStartObject();
            for (int i = 1; i <= columns.length; i++) {
                writeObjectField(jg, columns[i - 1], rs.getObject(i));
            }
            jg.writeEndObject();
        }
    }

    private static void writeListResultSetToJson(ResultSet rs, JsonGenerator jg, HashMap<Integer, String> columnNames) throws SQLException, IOException {
        String[] columns = getColumns(rs, columnNames);
        jg.writeStartArray();
        while (rs.next()) {
            jg.writeStartObject();
            for (int i = 1; i <= columns.length; i++) {
                writeObjectField(jg, columns[i - 1], rs.getObject(i));
            }
            jg.writeEndObject();
        }
        jg.writeEndArray();
    }

    private static String[] getColumns(ResultSet rs, HashMap<Integer, String> columnNames) throws SQLException {
        final ResultSetMetaData rsmd = rs.getMetaData();
        final int columnCount = rsmd.getColumnCount();
        String[] nombres = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            if (columnNames != null && columnNames.containsKey(i)) {
                nombres[i - 1] = columnNames.get(i);
            } else {
                nombres[i - 1] = rsmd.getColumnLabel(i);
                if (StringUtils.isEmpty(nombres[i - 1])) {
                    nombres[i - 1] = rsmd.getColumnName(i);
                }
                nombres[i - 1] = camelCase(nombres[i - 1]);
            }
        }
        return nombres;
    }

    private static String camelCase(String cadena) {
        if (cadena == null) {
            return "";
        }
        String result = "";
        boolean upper = false;
        for (int i = 0; i < cadena.length(); i++) {
            if (cadena.charAt(i) == '_') {
                upper = true;
            } else if (upper) {
                result += Character.toUpperCase(cadena.charAt(i));
                upper = false;
            } else {
                result += Character.toLowerCase(cadena.charAt(i));
            }
        }
        return result;
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
