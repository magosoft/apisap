/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lafuente.sap.dao.PGDatabase;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.exceptions.GELExceptionMapping;
import com.lafuente.sap.rest.dto.ReporteDTO;
import com.lafuente.sap.utils.JasperserverClient;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

/**
 *
 * @author dcaceres
 */
@Path("rp")
@Stateless
public class RPEndpoint {

    @Inject
    private PGDatabase daoPG;
    @Context
    private Configuration configuration;

    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response obtenerDatosDeActa(
            ReporteDTO dto
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            try {
                System.out.println("" + new String(mapper.writeValueAsBytes(dto)));
            } catch (JsonProcessingException ex) {
                throw new GELException(ex);
            }
            daoPG.openDatabase(configuration.getProperties());
            String sql = "SELECT url  FROM programacion.contratos WHERE nombre = '" + dto.getNombre() + "'";
            String url = "";
            //ResultSet 
            HashMap<String, Object> result = new HashMap();
            HashMap<String, String> queryParams = new HashMap<>();
            try {
                ResultSet rs = daoPG.rawQuery(sql, null);
                if (rs.next()) {
                    url = rs.getString("url");
                }

                //StringUtils.toEncodedString(bytes, charset)                 
            } catch (SQLException ex) {
                throw new GELException(ex);
            }/* catch (JsonProcessingException ex) {
                throw new GELException(ex);
            }*/

            //http://172.28.3.53:8080/jasperserver/rest_v2/reports/Varios/ReporteDesarrollo/acta_entrega.html?jsonEncode=
            JasperserverClient client = new JasperserverClient();
            //PdfWriter
            return Response.ok(client.getPdf(url, dto.getQueryParams()))
                    .header("Content-Disposition", "attachment; filename=\"" + result.get("vbeln").toString() + ".pdf\"")
                    .build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("{nombre}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPdf(
            @PathParam("nombre") String nombre
    ) {
        try {
            try {
                String path = configuration.getProperties().get("jboss.home.dir").toString();
                JasperReport jr = JasperCompileManager.compileReport(path + "/recursos/jrxml/" + nombre + ".jrxml");
                JasperPrint jp = JasperFillManager.fillReport(jr, new HashMap<>());
                byte[] result = JasperExportManager.exportReportToPdf(jp);
                return Response.ok(result)
                        .header("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"")
                        .build();
            } catch (JRException ex) {
                throw new GELException(ex);
            }
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        }
    }
}
