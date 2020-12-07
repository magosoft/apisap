/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest;

import com.lafuente.sap.dao.PGDatabase;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.exceptions.GELExceptionMapping;
import com.lafuente.sap.rest.stream.ListResultOutput;
import com.lafuente.sap.rest.stream.SingleResultOutput;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Guido
 */
@Path("ld")
@Stateless
public class LDEndpoint {

    @Inject
    private PGDatabase daoPG;
    @Context
    private Configuration configuration;

    //  SELECT * FROM public.zf_gel_s0001f6(in_nro_notaria integer));}
    @GET
    @Path("notarias")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listNotarias() {
        try {
            String query = "SELECT * FROM DBLINK('host=127.0.0.1 dbname=geldatabase user=gel password=gel*123',";
            query += "'SELECT * FROM public.zf_gel_s0001f6 (0)') AS t(cmnotnume NUMERIC,cmnotabog VARCHAR, cmnotdire VARCHAR, cmnottele VARCHAR, cmnotcelu VARCHAR, cmnotmail VARCHAR, cmnotlati NUMERIC, cmnotlong NUMERIC)";
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery(query, null);
            return Response.ok(new ListResultOutput(rs)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("notarias/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotaria(
            @PathParam("id") String id
    ) {
        try {
            String query = "SELECT * FROM DBLINK('host=127.0.0.1 dbname=geldatabase user=gel password=gel*123',";
            query += "'SELECT * FROM public.zf_gel_s0001f6 (" + id + ")') AS t(cmnotnume NUMERIC,cmnotabog VARCHAR, cmnotdire VARCHAR, cmnottele VARCHAR, cmnotcelu VARCHAR, cmnotmail VARCHAR, cmnotlati NUMERIC, cmnotlong NUMERIC)";
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery(query, null);
            return Response.ok(new SingleResultOutput(rs)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("contactos/{carnet}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarContactos(
            @PathParam("carnet") String carnet
    ) {
        try {
            String query = "SELECT * FROM DBLINK('host=127.0.0.1 dbname=geldatabase user=gel password=gel*123',";
            query += "'SELECT * FROM public.zf_gel_s0001f7 (''" + carnet + "'')') AS t(cliente VARCHAR,tipodoc VARCHAR, nrodocu VARCHAR, nrotelf VARCHAR)";
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery(query, null);
            return Response.ok(new SingleResultOutput(rs)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/agencias")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarAgencias(
            @PathParam("sociedad") String sociedad
    ) {
        try {
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery("SELECT * FROM seguridad.zf_gel_s0001f8('" + sociedad + "')", null);
            return Response.ok(new ListResultOutput(rs)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/firma-minutas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarFirmaMinutas(
            @PathParam("sociedad") String sociedad
    ) {
        try {
            String query = "SELECT * FROM DBLINK('host=127.0.0.1 dbname=geldatabase user=gel password=gel*123',";
            query += "'SELECT * FROM public.zf_gel_s0001f3 (''" + sociedad + "'' , '''')')";
            query += "AS t(matnr VARCHAR,maktx VARCHAR,atwrt01 VARCHAR,atwrt02 VARCHAR,atwrt03 VARCHAR,vbeln VARCHAR, partner VARCHAR,cmfltfech date,cmflthora VARCHAR,nomclien VARCHAR,tipodoc NUMERIC,cmcomndoc VARCHAR,cmcomedoc VARCHAR,cmcomntel VARCHAR)";
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery(query, null);
            return Response.ok(new ListResultOutput(rs)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/agenda/{carnet}/firma-minutas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarFirmaMinutas(
            @PathParam("sociedad") String sociedad,
            @PathParam("carnet") String carnet
    ) {
        try {
            String query = "SELECT * FROM DBLINK('host=127.0.0.1 dbname=geldatabase user=gel password=gel*123',";
            query += "'SELECT * FROM public.zf_gel_s0001f3 (''" + sociedad + "'' , ''" + carnet + "'')')";
            query += "AS t(matnr VARCHAR,maktx VARCHAR,atwrt01 VARCHAR,atwrt02 VARCHAR,atwrt03 VARCHAR,vbeln VARCHAR, partner VARCHAR,cmfltfech date,cmflthora VARCHAR,nomclien VARCHAR,tipodoc NUMERIC,cmcomndoc VARCHAR,cmcomedoc VARCHAR,cmcomntel VARCHAR)";
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery(query, null);
            return Response.ok(new ListResultOutput(rs)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/firma-protocolos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarFirmaProtocolos(
            @PathParam("sociedad") String sociedad
    ) {
        try {
            String query = "SELECT * FROM DBLINK('host=127.0.0.1 dbname=geldatabase user=gel password=gel*123',";
            query += "'SELECT * FROM public.zf_gel_s0001f4 (''" + sociedad + "'' , '''')')";
            query += "AS t(matnr VARCHAR,maktx VARCHAR,atwrt01 VARCHAR,atwrt02 VARCHAR,atwrt03 VARCHAR,vbeln VARCHAR, partner VARCHAR,cmfltfech date,cmflthora VARCHAR, cmfltnnot NUMERIC,nomclien VARCHAR,tipodoc NUMERIC,cmcomndoc VARCHAR,cmcomedoc VARCHAR,cmcomntel VARCHAR)";
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery(query, null);
            return Response.ok(new ListResultOutput(rs)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/agenda/{carnet}/firma-protocolos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarFirmaProtocolos(
            @PathParam("sociedad") String sociedad,
            @PathParam("carnet") String carnet
    ) {
        try {
            String query = "SELECT * FROM DBLINK('host=127.0.0.1 dbname=geldatabase user=gel password=gel*123',";
            query += "'SELECT * FROM public.zf_gel_s0001f4 (''" + sociedad + "'' , ''" + carnet + "'')')";
            query += "AS t(matnr VARCHAR,maktx VARCHAR,atwrt01 VARCHAR,atwrt02 VARCHAR,atwrt03 VARCHAR,vbeln VARCHAR, partner VARCHAR,cmfltfech date,cmflthora VARCHAR, cmfltnnot NUMERIC,nomclien VARCHAR,tipodoc NUMERIC,cmcomndoc VARCHAR,cmcomedoc VARCHAR,cmcomntel VARCHAR)";
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery(query, null);
            return Response.ok(new ListResultOutput(rs)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/entrega-testimonios")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarEntregaTestimonios(
            @PathParam("sociedad") String sociedad
    ) {
        try {
            String query = "SELECT * FROM DBLINK('host=127.0.0.1 dbname=geldatabase user=gel password=gel*123',";
            query += "'SELECT * FROM public.zf_gel_s0001f5 (''" + sociedad + "'' , '''')')";
            query += "AS t(matnr VARCHAR,maktx VARCHAR,atwrt01 VARCHAR,atwrt02 VARCHAR,atwrt03 VARCHAR,vbeln VARCHAR, partner VARCHAR,cmfltfech date,cmflthora VARCHAR,nomclien VARCHAR,tipodoc NUMERIC,cmcomndoc VARCHAR,cmcomedoc VARCHAR,cmcomntel VARCHAR)";
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery(query, null);
            return Response.ok(new ListResultOutput(rs)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/agenda/{carnet}/entrega-testimonios")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarEntregaTestimonios(
            @PathParam("sociedad") String sociedad,
            @PathParam("carnet") String carnet
    ) {
        try {
            String query = "SELECT * FROM DBLINK('host=127.0.0.1 dbname=geldatabase user=gel password=gel*123',";
            query += "'SELECT * FROM public.zf_gel_s0001f5 (''" + sociedad + "'' , ''" + carnet + "'')')";
            query += "AS t(matnr VARCHAR,maktx VARCHAR,atwrt01 VARCHAR,atwrt02 VARCHAR,atwrt03 VARCHAR,vbeln VARCHAR, partner VARCHAR,cmfltfech date,cmflthora VARCHAR,nomclien VARCHAR,tipodoc NUMERIC,cmcomndoc VARCHAR,cmcomedoc VARCHAR,cmcomntel VARCHAR)";
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery(query, null);
            return Response.ok(new ListResultOutput(rs)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    private void i(String message) {
        if (!StringUtils.isEmpty(message)) {
            Logger.getLogger("LD")
                    .log(Level.INFO, message);
        }
    }
}
