/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author Guido
 */
public class MapDatabase extends Database {

    public MapDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            e(ex.getMessage());
        }
    }

    @Override
    protected void setParameterDate(PreparedStatement ps, int i, Date date) throws SQLException {

    }

    @Override
    protected String url() {
        return "jdbc.map.url";
    }

    @Override
    protected String user() {
        return "jdbc.map.user";
    }

    @Override
    protected String passwd() {
        return "jdbc.map.passwd";
    }

}
