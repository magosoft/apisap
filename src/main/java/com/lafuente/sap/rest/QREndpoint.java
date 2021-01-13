/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lafuente.qr.api.ApiQR;
import com.lafuente.qr.api.RequestQRDTO;
import com.lafuente.qr.api.ResponseQRDTO;
import com.lafuente.sap.dao.PGDatabase;
import com.lafuente.sap.dao.SPDao;
import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.exceptions.GELExceptionMapping;
import com.lafuente.sap.rest.dto.PagoDTO;
import com.lafuente.sap.ws.HelperSAP;
import com.lafuente.sap.ws.ServicioSAP;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobrConsCuotasETt;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasEcStr;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasEcTt;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasIcTt;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
@Path("qr")
@Stateless
public class QREndpoint {

    @Inject
    private SPDao dao;
    @Inject
    private PGDatabase daoPG;
    @Context
    private Configuration configuration;

    private String asicon;

    @POST
    @Path("generar-pago-contratos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response simpleQRContratos(PagoDTO dto) {
        return qr(dto, HelperSAP.CONTRATO);
    }

    @POST
    @Path("generar-pago-reservas")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response simpleQRReservas(PagoDTO dto) {
        return qr(dto, HelperSAP.RESERVA);
    }

    private Response qr(PagoDTO dto, String tipoDoc) {
        try {
            HelperSAP.validarDTO(dto);
            i(dto.toString());
            String user = "";
            String bank = "";
            dao.openDatabase(configuration.getProperties());
            ResultSet r = dao.rawQuery("SELECT DISTINCT bukrs,hbkid,usnam FROM SAPABAP1.ZFI_BANK_PRO_WS WHERE codpro = '" + dto.getMatnr() + "' and serban = 'QR'", null);
            try {
                if (r.next()) {
                    bank = r.getString("hbkid");
                    user = r.getString("usnam");
                }
                i("CAJERO: " + user);
                i("BANCO: " + bank);
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            if (bank.isEmpty()) {
                throw new GELException(CodeError.GEL30020);
            }
            int cantidadHistorico = 0;
            if (HelperSAP.CONTRATO.equals(tipoDoc)) {
                r = dao.rawQuery("SELECT COUNT(B.NUM_CUOTA) AS CANT FROM sapabap1.ZSD_HISTC01 A, SAPABAP1.ZSD_HISTC02 B WHERE B.VBELN = CASE WHEN LENGTH(NUM_CONTRATO_SAI) = 0 THEN NUM_CONTRATO_SAP ELSE NUM_CONTRATO_SAI END AND A.NUM_CONTRATO_SAP = '" + dto.getDocven() + "'", null);
                try {
                    if (r.next()) {
                        cantidadHistorico = r.getInt("CANT");
                    }
                } catch (SQLException ex) {
                    throw new GELException(CodeError.GEL20000, ex);
                }
            }
            ServicioSAP serviceS = new ServicioSAP(configuration.getProperties(), bank, user);
            ZfiWsCobrConsCuotasETt items = serviceS.obtenerCuotas(dto, HelperSAP.RESERVA.equals(tipoDoc));
            HelperSAP.getZfiWsCobranzasIcTt(items, dto, user, tipoDoc, cantidadHistorico);
            ApiQR api = new ApiQR();
            ResponseQRDTO result = api.generar(dto);
            insertarQR(bank, user, tipoDoc, result, dto);
            return Response.ok(result).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    private void insertarQR(String bank, String user, String tipoDoc, ResponseQRDTO result, PagoDTO dto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            daoPG.openDatabase(configuration.getProperties());
            if (result.isSuccess()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("usuario", user);
                fila.put("banco", bank);
                fila.put("id_qr", result.getId());
                fila.put("code_qr", result.getQr());
                fila.put("tipo_doc", tipoDoc);
                fila.put("estado", 1);
                fila.put("expiracion", getDate(result.getExpiracion()));
                fila.put("fecha_creacion", new Timestamp(new Date().getTime()));
                try {
                    String json = mapper.writeValueAsString(dto);
                    fila.put("dato", json);
                } catch (JsonProcessingException e) {
                }
                daoPG.insert("pagos.qr_response", fila);
            }
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @POST
    @Path("ReceiveNotification")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response receiveNotification(RequestQRDTO dto) {
        i("BNB: " + dto.toString());
        Map<String, Object> resultado = new HashMap<>();
        try {
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery("SELECT * FROM pagos.obtener_qr_response('" + dto.getQrId() + "')", null);
            try {
                if (rs.next()) {
                    String json = rs.getString("dato");
                    String tipoDoc = rs.getString("tipo_doc");
                    resultado = cobrarQR(json, tipoDoc, dto.getQrId());
                    if ((boolean) resultado.get("success") == true) {
                        rs = daoPG.rawQuery("SELECT pagos.cobrar_qr_response('" + dto.getQrId() + "')", null);
                        rs.next();
                    }
                } else {
                    resultado.put("success", false);
                    resultado.put("message", "No existe ID: " + dto.getQrId());
                }
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
        } catch (GELException ex) {
            resultado.put("success", false);
            resultado.put("message", ex.getMessage());
        } finally {
            daoPG.closeDatabase();
        }
        return Response.ok(resultado).build();
    }

    /*@GET
    @Path("cobrar-qr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pagarQR() {
        Map<String, Object> resultado = new HashMap<>();
        try {
            ApiQR api = new ApiQR();
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery("SELECT *,COALESCE(fecha_proceso,'1900-01-01') as fecha FROM pagos.qr_response WHERE estado = 1 ORDER BY fecha LIMIT 1", null);
            try {
                Map<String, Object> fila = new HashMap<>();
                Timestamp time = new Timestamp(new Date().getTime());
                fila.put("fecha_proceso", time);
                if (rs.next()) {
                    long id = rs.getLong("id");
                    String idQr = rs.getString("id_qr");
                    ResponseEstadoQRDTO result = api.getEstado(idQr);
                    if (result.getStatusId() == 2) {
                        fila.put("estado", 2);
                        fila.put("observacion", "QR USADO EXITOSAMENTE!");
                        fila.put("fecha_cobrada", time);
                        resultado.put("status", "QR USADO EXITOSAMENTE!");
                    } else if (result.getStatusId() == 3) {
                        fila.put("estado", 3);
                        fila.put("observacion", "QR EXPIRADO");
                        resultado.put("status", "QR EXPIRADO");
                    } else {
                        resultado.put("status", "QR NO USADO");
                    }
                    daoPG.update("pagos.qr_response", fila, "id = ?", new Object[]{id});
                }
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
        return Response.ok(resultado).build();
    }*/
    private Date getDate(String fechaStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("yyyy-MM-dd");
            return format.parse(fechaStr);
        } catch (ParseException ex) {
            return null;
        }
    }

    private Map<String, Object> cobrarQR(String json, String tipoDoc, String transaccion) throws GELException {
        PagoDTO dto;
        try {
            dto = new ObjectMapper().readValue(json, PagoDTO.class);
        } catch (JsonProcessingException ex) {
            throw new GELException(ex);
        }
        try {
            HelperSAP.validarDTO(dto);
            i(dto.toString());
            String user = "";
            String bank = "";
            dao.openDatabase(configuration.getProperties());
            ResultSet r = dao.rawQuery("SELECT DISTINCT bukrs,hbkid,usnam FROM SAPABAP1.ZFI_BANK_PRO_WS WHERE codpro = '" + dto.getMatnr() + "' and serban = 'QR'", null);
            try {
                if (r.next()) {
                    bank = r.getString("hbkid");
                    user = r.getString("usnam");
                }
                i("CAJERO: " + user);
                i("BANCO: " + bank);
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            if (bank.isEmpty()) {
                throw new GELException(CodeError.GEL30020);
            }

            int cantidadHistorico = 0;
            if (HelperSAP.CONTRATO.equals(tipoDoc)) {
                r = dao.rawQuery("SELECT COUNT(B.NUM_CUOTA) AS CANT FROM sapabap1.ZSD_HISTC01 A, SAPABAP1.ZSD_HISTC02 B WHERE B.VBELN = CASE WHEN LENGTH(NUM_CONTRATO_SAI) = 0 THEN NUM_CONTRATO_SAP ELSE NUM_CONTRATO_SAI END AND A.NUM_CONTRATO_SAP = '" + dto.getDocven() + "'", null);
                try {
                    if (r.next()) {
                        cantidadHistorico = r.getInt("CANT");
                    }
                } catch (SQLException ex) {
                    throw new GELException(CodeError.GEL20000, ex);
                }
            }
            ServicioSAP serviceS = new ServicioSAP(configuration.getProperties(), bank, user);
            ZfiWsCobrConsCuotasETt items = serviceS.obtenerCuotas(dto, HelperSAP.RESERVA.equals(tipoDoc));
            ZfiWsCobranzasIcTt i0 = HelperSAP.getZfiWsCobranzasIcTt(items, dto, user, tipoDoc, cantidadHistorico);
            i0.getItem().forEach((elem) -> {
                elem.setNumcom(transaccion);
            });
            ZfiWsCobranzasEcTt r1 = serviceS.cobrar(i0);
            if (serviceS.hasErrorCobro(r1)) {
                throw new GELException(CodeError.GEL30021);
            }
            asicon = insertar(r1, transaccion, BigDecimal.ZERO, "BOB");

            return getResult(r1);
        } catch (GELException ex) {
            throw ex;
        } finally {
            dao.closeDatabase();
        }

    }

    private String insertar(ZfiWsCobranzasEcTt r1, String transaccion, BigDecimal monto, String moneda) {
        try {
            daoPG.openDatabase(configuration.getProperties());
            asicon = "";
            final List<ZfiWsCobranzasEcStr> lista = r1.getItem();
            for (int i = lista.size(); i > 0 ; i--) {
                ZfiWsCobranzasEcStr elem = lista.get(i-1);
                Map<String, Object> fila = new HashMap<>();
                if (asicon.isEmpty()) {
                    asicon = elem.getAsicon();
                }
                fila.put("doccon", elem.getDoccon());
                fila.put("asicon", asicon);
                fila.put("asicon_sap", elem.getAsicon());
                fila.put("seqcuo", elem.getSeqcuo());
                fila.put("tipdoc", elem.getTipdoc());
                fila.put("numcom", transaccion);
                fila.put("impcob", monto);
                fila.put("moncob", moneda);
                fila.put("numid", elem.getNumid());
                fila.put("nomcli", elem.getNomcli());
                fila.put("maktx", elem.getMaktx());
                fila.put("datter", elem.getDatter());
                fila.put("feccob", elem.getFeccob());
                fila.put("hora", elem.getHora());
                fila.put("impori", elem.getImpori());
                fila.put("impdes", elem.getImpdes());
                fila.put("imprec", elem.getImprec());
                fila.put("impcuo", elem.getImpcuo());
                fila.put("moncuo", elem.getMoncuo());
                daoPG.insert("pagos.transaccion_response", fila);
            }            
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
        return asicon;
    }

    private Map<String, Object> getResult(ZfiWsCobranzasEcTt r1) {
        i("[PAGO EXITOSO] CLIENTE: " + r1.getItem().get(0).getNomcli() + ", HORA: " + r1.getItem().get(0).getHora() + ", DATO TERRENO: " + r1.getItem().get(0).getDatter());
        HashMap<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "OK");
        return res;
    }

    private void i(String message) {
        if (!StringUtils.isEmpty(message)) {
            Logger.getLogger("QR").log(Level.INFO, message);
        }
    }

}
