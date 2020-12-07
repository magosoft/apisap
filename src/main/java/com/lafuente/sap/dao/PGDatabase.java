/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author guido
 */
public class PGDatabase extends Database {

    public PGDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            e(ex.getMessage());
        }
    }

    @Override
    protected void setParameterDate(PreparedStatement ps, int i, Date date) throws SQLException {
        ps.setDate(i, new java.sql.Date(date.getTime()));
    }

    @Override
    protected String url() {
        return "jdbc.db.url";
    }

    @Override
    protected String user() {
        return "jdbc.db.user";
    }

    @Override
    protected String passwd() {
        return "jdbc.db.passwd";
    }

    

}
