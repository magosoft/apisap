/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lafuente.sap.dao.PGDatabase;
import com.lafuente.sap.dao.SPDao;
import com.lafuente.sap.dao.SPQuery;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.exceptions.GELExceptionMapping;
import com.lafuente.sap.rest.stream.ArrayOutput;
import com.lafuente.sap.rest.stream.ResultSetToJsonMapper;
import com.lafuente.sap.utils.JasperserverClient;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Guido
 */
@Path("ld")
@Stateless
public class LDEndpoint {

    @Inject
    private SPDao dao;
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
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
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
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.singleResultSet(rs))).build();
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
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.singleResultSet(rs))).build();
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
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
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
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
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
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
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
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
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
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
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
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
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
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    private void i(String message) {
        if (!StringUtils.isEmpty(message)) {
            Logger.getLogger("LD").log(Level.INFO, message);
        }
    }

    //https://apis100-dev.grupo-lafuente.com/api-sap/v2/ld/visitas/'+idVisita+'/contratos/'+vbeln+'/acta-entrega-pdf
    @GET
    @Path("visitas/{idVisita}/contratos/{vbeln}/acta-entrega-pdf")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerDatosDeActa(
            @PathParam("idVisita") String id,
            @PathParam("vbeln") String vbeln,
            @QueryParam("usuario") String usuario,
            @QueryParam("nombreUsuario") String nombreUsuario
    ) {
        try {
            daoPG.openDatabase(configuration.getProperties());
            dao.openDatabase(configuration.getProperties());
            String sql = "SELECT id, id_proyecto,uv,mz,lote FROM programacion.contratos WHERE id_programacion = " + id + " AND contrato = '" + vbeln + "'";
            String matnr = "";
            String uv = "";
            String mz = "";
            String lt = "";
            String numero = "";
            //ResultSet 
            HashMap<String, Object> result = new HashMap();
            HashMap<String, Object> queryParams = new HashMap<>();
            try {
                ResultSet rs = daoPG.rawQuery(sql, null);
                if (rs.next()) {
                    numero = rs.getString("id");
                    matnr = rs.getString("id_proyecto");
                    uv = rs.getString("uv");
                    mz = rs.getString("mz");
                    lt = rs.getString("lote");
                } else {
                    throw new GELException("No existe contrato: " + vbeln);
                }
                sql = "SELECT DISTINCT PROYECTO, DESCRIPCION, PROPIETARIO, COMERCIALIZA FROM SAPABAP1.ZSD_PROYECTO WHERE TO_INTEGER(PROYECTO) = " + matnr;
                rs = dao.rawQuery(sql, null);
                String sociedad = "";
                if (rs.next()) {
                    result.put("matnr", rs.getString("PROYECTO"));
                    result.put("maktx", rs.getString("DESCRIPCION"));
                    sociedad = rs.getString("COMERCIALIZA");
                    result.put("bukrs", rs.getString("PROPIETARIO"));
                }
                rs = dao.ejecutarConsulta(SPQuery.OBTENER_LOTE, new Object[]{matnr, uv, mz, lt});
                if (rs.next()) {
                    result.put("codigoProyecto", matnr);
                    result.put("atwrt01", rs.getString("uv"));
                    result.put("atwrt02", rs.getString("mz"));
                    result.put("atwrt03", rs.getString("lt"));
                    result.put("superficie", rs.getString("superficie"));
                    result.put("bsark", rs.getString("categoria"));
                    result.put("colNorte", rs.getString("colindancia_norte"));
                    result.put("colNorteMed", rs.getBigDecimal("colindancia_norte_medida"));
                    result.put("colSur", rs.getString("colindancia_sur"));
                    result.put("colSurMed", rs.getBigDecimal("colindancia_sur_medida"));
                    result.put("colEste", rs.getString("colindancia_este"));
                    result.put("colEsteMed", rs.getBigDecimal("colindancia_este_medida"));
                    result.put("colOeste", rs.getString("colindancia_oeste"));
                    result.put("colOesteMed", rs.getBigDecimal("colindancia_oeste_medida"));
                }

                rs = dao.ejecutarConsulta(SPQuery.OBTENER_CONTRATO_POR_LOTE, new Object[]{matnr, uv, mz, lt});
                if (rs.next()) {
                    result.put("vbeln", rs.getString("ilmprnpre"));
                    result.put("vgbel", rs.getString("ilmprvgbl"));
                    result.put("vsartDesc", rs.getInt("ilmprtven") == 5 ? "VENTA CONTADO" : "VENTA A CREDITO");
                    result.put("kunnr", rs.getString("ilmprcage"));
                    int x = rs.getInt("ilmprstat");
                    result.put("statDesc", x == 0 ? "ACTIVO" : x == 9 ? "CANCELADO" : "DESCONOCIDO");
                }
                rs = dao.ejecutarConsulta(SPQuery.LIST_CLIENTES, new Object[]{sociedad, result.get("kunnr").toString(), ""});
                if (rs.next()) {
                    if ("ZID001".equals(rs.getString("type"))) {
                        String idnumber = rs.getString("idnumber");
                        String[] str = idnumber.trim().split(" ");
                        if (str.length > 0) {
                            result.put("numDoc", str[0]);
                        }
                        if (str.length > 1) {
                            result.put("extDoc", str[1]);
                        }
                    } else {
                        result.put("numDoc", rs.getString("idnumber"));
                    }
                    String nombreCompleto = StringUtils.trim(rs.getString("name_first"));
                    nombreCompleto = nombreCompleto + " " + nvl(rs.getString("name_last"));
                    nombreCompleto = nombreCompleto + " " + nvl(rs.getString("name_last2"));
                    nombreCompleto = nombreCompleto + " " + nvl(rs.getString("prefix1_desc"));
                    nombreCompleto = nombreCompleto + " " + nvl(rs.getString("name_lst2"));
                    result.put("fullName", nvl(nombreCompleto));
                    result.put("strSuppl", nvl(rs.getString("str_suppl")));
                    result.put("smtpAddr", nvl(rs.getString("smtp_addr")));
                    result.put("telNumber1", nvl(rs.getString("tel_number1")));
                }
                ObjectMapper mapper = new ObjectMapper();
                result.put("usuario", nvl(usuario));
                result.put("nombreUsuario", nvl(nombreUsuario));
                result.put("numero", numero);
                //StringUtils.toEncodedString(bytes, charset)                 
                queryParams.put("jsonEncode", Base64.getEncoder().encodeToString(mapper.writeValueAsBytes(result)));

            } catch (SQLException ex) {
                throw new GELException(ex);
            } catch (JsonProcessingException ex) {
                throw new GELException(ex);
            }
            try {
                try {
                    String path = configuration.getProperties().get("jboss.home.dir").toString();
                    JasperReport jr = JasperCompileManager.compileReport(path + "/recursos/jrxml/acta_entrega.jrxml");
                    JasperPrint jp = JasperFillManager.fillReport(jr, queryParams);
                    byte[] data = JasperExportManager.exportReportToPdf(jp);
                    return Response.ok(data)
                            .header("Content-Disposition", "attachment; filename=\"" + result.get("vbeln").toString() + ".pdf\"")
                            .build();
                } catch (JRException ex) {
                    throw new GELException(ex);
                }
            } catch (GELException ex) {
                throw new GELExceptionMapping(ex);
            }
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }

    }

    private String nvl(String value) {
        if (value == null) {
            return "";
        }
        if ("null".equals(value)) {
            return "";
        }
        return StringUtils.trim(value);
    }

}
