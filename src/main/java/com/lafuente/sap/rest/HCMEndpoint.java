/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest;

import com.lafuente.sap.dao.SPDao;
import com.lafuente.sap.dao.SPQuery;
import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.exceptions.GELExceptionMapping;
import com.lafuente.sap.rest.stream.ArrayOutput;
import com.lafuente.sap.rest.stream.ResultSetToJsonMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author dcaceres
 */
@Path("hcm")
@Stateless
public class HCMEndpoint {

    @Inject
    private SPDao dao;

    @Context
    private Configuration configuration;

    @GET
    @Path("dias-vacacion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerDiaVacacion(
            @QueryParam("fechaInicio") String fechaInicio,
            @QueryParam("dias") Integer dias,
            @QueryParam("sucursal") Integer sucursal,
            @QueryParam("ciudad") Integer ciudad,
            @QueryParam("provincia") String provincia
    ) {
        try {
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.DIAS_HABILES_VACACION, new Object[]{fechaInicio, dias, sucursal, ciudad, provincia});
            HashMap<String, Object> result = null;
            try {
                if (rs.next()) {
                    result = new HashMap<>();
                    result.put("fechaInicio", fechaInicio);
                    result.put("fechaFin", rs.getString("fecha_fin"));
                    result.put("diasVacacion", dias);
                    result.put("diasTotal", rs.getInt("dia"));
                }
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            return Response.ok(result).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }
    
    @GET
    @Path("dia-festivo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarClientesPorComercializadora( 
            @QueryParam("fecha") String fecha,
            @QueryParam("sucursal") Integer sucursal,
            @QueryParam("ciudad") Integer ciudad,
            @QueryParam("provincia") String provincia
    ) {
        try {
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.DIAS_FESTIVOS, new Object[]{fecha, sucursal, ciudad, provincia});
            HashMap<String, Object> result = null;
            try {
                if (rs.next()) {
                    result = new HashMap<>();
                    result.put("fecha", fecha);
                    result.put("esFestivo", rs.getString("dia_festivo"));                    
                }
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            return Response.ok(result).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET
    @Path("empleados/{kunnr}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response formasDePagos(@PathParam("kunnr") String kunnr) {
        try {
            kunnr = "0000000000" + kunnr;
            kunnr = kunnr.substring(kunnr.length() - 10);
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.DATOS_EMPLEADO, new Object[]{kunnr});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }

    }

    @GET
    @Path("empresas/{bukrs}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response empresas(@PathParam("bukrs") String bukrs) {
        try {
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_EMPRESAS, new Object[]{bukrs});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }

    }

    @GET
    @Path("empresas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response empresas() {
        try {
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_EMPRESAS, new Object[]{""});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }

    }
     private void i(String message) {        
        if (!StringUtils.isEmpty(message)) {
            Logger.getLogger("HCM")
                    .log(Level.INFO, message);
        }
    }
}
