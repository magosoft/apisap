package com.lafuente.sap.dao;

import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author GUIDO CACERES PINTO
 */
public class SPDao extends SAPDatabase {

    private ResultSet result;

    @Override
    public void closeDatabase() {
        if (result != null) {
            try {
                result.close();
            } catch (SQLException ex) {
                e(ex.getMessage());
            }
        }
        result = null;
        super.closeDatabase();
    }

    public ResultSet ejecutarConsulta(SPQuery query, Object[] values) throws GELException {
        try {
            CallableStatement cstmt = this.conn.prepareCall("{ CALL " + query.consulta() + " }");
            loadParameters(cstmt, values);
            print("{ CALL " + query.consulta() + " }", values);
            result = cstmt.executeQuery();            
        } catch (SQLException ex) {
            throw new GELException(CodeError.GEL20000, ex);
        }
        return result;
    }
    
}
