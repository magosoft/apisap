package com.lafuente.sap.dao;

import com.lafuente.sap.utils.SAPUtils;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author GUIDO CACERES PINTO
 */
public class SAPDatabase extends Database {

    public SAPDatabase() {
        try {
            Class.forName("com.sap.db.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            e(ex.getMessage());
        }
    }

    @Override
    protected void setParameterDate(PreparedStatement stmt, int i, Date value) throws SQLException {
        stmt.setString(i, SAPUtils.dateToStr(value));
    }

    @Override
    protected String url() {
        return "jdbc.sap.url";
    }

    @Override
    protected String user() {
        return "jdbc.sap.user";
    }

    @Override
    protected String passwd() {
        return "jdbc.sap.passwd";
    }
}
