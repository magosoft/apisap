package com.lafuente.sap.dao;

import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Guido
 */
public abstract class Database {

    protected Connection conn;

    protected abstract String url();

    protected abstract String user();

    protected abstract String passwd();

    public Connection openDatabase(Map<String, Object> config) throws GELException {
        try {
            conn = (Connection) DriverManager.getConnection((String) config.get(url()), (String) config.get(user()), (String) config.get(passwd()));
        } catch (SQLException ex) {
            throw new GELException(CodeError.GEL20000, ex);
        }
        return conn;
    }

    public void closeDatabase() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                e(ex.getMessage());
            }
        }
        conn = null;
    }

    public int insert(String table, Map<String, Object> values) throws GELException {
        if (values == null || values.isEmpty()) {
            throw new GELException(CodeError.GEL20001);
        }
        int result = -1;
        int size = values.size();
        if (size > 0) {
            String sql = "INSERT INTO " + table + " (";
            Object[] bindArgs = new Object[size];
            int i = 0;
            String comma = "";
            String args = "";
            for (String colName : values.keySet()) {
                sql += comma + colName;
                args += comma + '?';
                comma = ",";
                bindArgs[i++] = values.get(colName);
            }
            sql += ") VALUES (" + args + ")";
            print(sql, bindArgs);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                loadParameters(stmt, bindArgs);
                result = stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
        }
        return result;
    }

    public int update(String table, Map<String, Object> values, String whereClause, String[] whereArgs) throws GELException {
        if (values == null || values.isEmpty()) {
            throw new GELException(CodeError.GEL20002);
        }
        int result = -1;
        String sql = "UPDATE " + table + " SET ";
        int setValuesSize = values.size();
        int bindArgsSize = (whereArgs == null) ? setValuesSize : (setValuesSize + whereArgs.length);
        Object[] bindArgs = new Object[bindArgsSize];
        int i = 0;
        String comma = "";
        for (String colName : values.keySet()) {
            sql += comma + colName + "=?";
            comma = ",";
            bindArgs[i++] = values.get(colName);
        }
        if (whereArgs != null) {
            for (i = setValuesSize; i < bindArgsSize; i++) {
                bindArgs[i] = whereArgs[i - setValuesSize];
            }
        }
        if (!StringUtils.isEmpty(whereClause)) {
            sql += " WHERE " + whereClause;
        }
        print(sql, bindArgs);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            loadParameters(stmt, bindArgs);
            result = stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new GELException(CodeError.GEL20000, ex);
        }
        return result;
    }

    public int delete(String table, String whereClause, Object[] whereArgs) throws GELException {
        int result = -1;
        String sql = "DELETE FROM " + table + (!StringUtils.isEmpty(whereClause) ? " WHERE" + whereClause : "");
        print(sql, whereArgs);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            loadParameters(stmt, whereArgs);
            result = stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new GELException(CodeError.GEL20000, ex);
        }
        return result;
    }

    public ResultSet rawQuery(String sql, Object[] arg) throws GELException {
        ResultSet result = null;
        try {
            print(sql, arg);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                loadParameters(stmt, arg);
                result = stmt.executeQuery();
            }
        } catch (SQLException ex) {
            throw new GELException(CodeError.GEL20000, ex);
        }
        return result;

    }

    protected void loadParameters(PreparedStatement stmt, Object[] values) throws SQLException {
        if (values == null) {
            return;
        }
        for (int i = 1; i <= values.length; i++) {
            Object value = values[i - 1];
            if (value == null) {
                stmt.setNull(i, java.sql.Types.NULL);
            } else if (value instanceof Integer) {
                stmt.setInt(i, (Integer) value);
            } else if (value instanceof Boolean) {
                stmt.setBoolean(i, (Boolean) value);
            } else if (value instanceof Timestamp) {
                stmt.setTimestamp(i, (Timestamp) value);
            } else if (value instanceof Date) {
                setParameterDate(stmt, i, (Date) value);
            } else if (value instanceof Long) {
                stmt.setLong(i, (Long) value);
            } else if (value instanceof Double) {
                stmt.setDouble(i, (Double) value);
            } else if (value instanceof Float) {
                stmt.setFloat(i, (Float) value);
            } else if (value instanceof BigDecimal) {
                stmt.setBigDecimal(i, (BigDecimal) value);
            } else if (value instanceof Byte) {
                stmt.setByte(i, (Byte) value);
            } else if (value instanceof byte[]) {
                stmt.setBytes(i, (byte[]) value);
            } else {
                stmt.setString(i, (String) value);
            }
        }
    }

    protected abstract void setParameterDate(PreparedStatement stmt, int i, Date value) throws SQLException;

    protected void e(String message) {
        if (!StringUtils.isEmpty(message)) {
            Logger.getLogger("SQL").log(Level.SEVERE, message);
        }

    }

    /*protected void i(String message) {
        if (!StringUtils.isEmpty(message)) {
            Logger.getLogger("API-SAP")
                    .log(Level.INFO, message);
        }
    }*/
    protected void print(String sql, Object[] args) {
        if (!StringUtils.isEmpty(sql) && args != null) {
            String lineas = "PARAMETROS: ";
            char comma = ' ';
            for (int i = 1; i <= args.length; i++) {
                lineas += comma + Integer.toString(i) + " => " + args[i - 1];
                comma = ',';
            }
            Logger.getLogger("SQL").log(Level.INFO, sql);
            Logger.getLogger("SQL").log(Level.INFO, lineas);
            return;
        }
        if (!StringUtils.isEmpty(sql)) {
            Logger.getLogger("SQL").log(Level.INFO, sql);
        }

    }
}
