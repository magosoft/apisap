package com.lafuente.sap.rest;

import com.lafuente.linkser.ws.ServicioLINKSER;
import com.lafuente.sap.dao.MapDatabase;
import com.lafuente.sap.dao.PGDatabase;
import com.lafuente.sap.dao.SPDao;
import com.lafuente.sap.dao.SPQuery;
import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.exceptions.GELExceptionMapping;
import com.lafuente.sap.rest.dto.PagoDTO;
import com.lafuente.sap.rest.dto.ReservaDTO;
import com.lafuente.sap.rest.dto.SolicitudCompraDTO;
import com.lafuente.sap.rest.stream.ArrayOutput;
import com.lafuente.sap.rest.stream.ResultSetToJsonMapper;
import com.lafuente.sap.utils.SAPUtils;
import com.lafuente.sap.ws.HelperSAP;
import com.lafuente.sap.ws.ServicioSAP;
import com.lafuente.sap.ws.ServicioSAPGEL;
import com.lafuente.tigomoney.ws.ServicioTIGOMONEY;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobrConsCuotasETt;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasEcStr;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasEcTt;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasIcTt;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
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
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author GUIDO CACERES PINTO
 */
@Path("sd")
@Stateless
public class SDEndpoint {

    @Inject
    private SPDao dao;

    @Inject
    private PGDatabase daoPG;
    @Inject
    private MapDatabase daoMap;
    @Context
    private Configuration configuration;

    private String asicon;

    /*@PostConstruct
    public void init() {
        
    }*/

 /* @PreDestroy
    public void end() {
        System.out.println("FIN DATABASE");
        dao.closeDatabase();
        daoPG.closeDatabase();
        daoMap.closeDatabase();
    }*/
    @GET
    @Path("{sociedad}/clientes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarClientesPorComercializadora(
            @PathParam("sociedad") String sociedad,
            @QueryParam("numeroDeudor") String numeroDeudor,
            @QueryParam("numeroDocumento") String numeroDocumento
    ) {
        try {
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_CLIENTES, new Object[]{sociedad, numeroDeudor, numeroDocumento});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/clientes/{numeroDeudor}/reservas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarReservasPorDeudor(
            @PathParam("sociedad") String sociedad,
            @PathParam("numeroDeudor") String numeroDeudor
    ) {
        try {
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_RESERVAS, new Object[]{sociedad, numeroDeudor});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/clientes/{numeroDeudor}/reservas-pago")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarReservasNoCobradasPorDeudor(
            @PathParam("sociedad") String sociedad,
            @PathParam("numeroDeudor") String numeroDeudor,
            @QueryParam("modres") String modres
    ) {
        try {
            if (StringUtils.isEmpty(modres)) {
                modres = "S001M003";
            }
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_RESERVAS_POR_PAGAR, new Object[]{sociedad, numeroDeudor, modres});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET
    @Path("reservas/{documentoVenta}/cuotas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarCuotasPorReserva(
            @PathParam("documentoVenta") String documentoVenta,
            @QueryParam("estadoCuota") String estadoCuota
    ) {
        try {
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_RESERVAS_CUOTAS, new Object[]{documentoVenta, estadoCuota});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @POST
    @Path("reservas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearReserva(
            ReservaDTO dto
    ) {
        try {
            if (dto == null) {

            }
            if (StringUtils.isEmpty(dto.getKunnr())) {

            }
            String valor = "0000000000" + dto.getKunnr();
            dto.setKunnr(valor.substring(valor.length() - 10));
            if (StringUtils.isEmpty(dto.getMatnr())) {

            }
            valor = "000000000000000000" + dto.getMatnr();
            dto.setMatnr(valor.substring(valor.length() - 18));
            if (StringUtils.isEmpty(dto.getLote())) {

            }
            if (StringUtils.isEmpty(dto.getTipoVenta())) {

            }
            if (StringUtils.isEmpty(dto.getPlazo())) {

            }
            if (dto.getNetwr() == null) {

            }
            daoMap.openDatabase(configuration.getProperties());
            ServicioSAPGEL serviceS = new ServicioSAPGEL(configuration.getProperties(), "SYSTEM");
            dto.setVbeln(serviceS.registrar(dto.getKunnr(), dto.getLote(), dto.getMatnr(), dto.getNetwr(), dto.getPlazo(), dto.getTipoVenta()));
            if (!"0".equals(dto.getVbeln())) {
                daoMap.rawQuery("SELECT public.cambiar_estado_por_reserva(" + dto.getMatnr().substring(dto.getMatnr().length() - 3) + ", '" + dto.getLote() + "')", null);
            }
            return Response.ok(dto).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoMap.closeDatabase();
            dao.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/clientes/{numeroDeudor}/contratos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarContratosPorDeudor(
            @PathParam("sociedad") String sociedad,
            @PathParam("numeroDeudor") String numeroDeudor
    ) {
        try {
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_CONTRATOS, new Object[]{sociedad, numeroDeudor});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/clientes/{numeroDeudor}/contratos-pago")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarContratosNoCobradosPorDeudor(
            @PathParam("sociedad") String sociedad,
            @PathParam("numeroDeudor") String numeroDeudor,
            @QueryParam("modcon") String modcon
    ) {
        try {
            if (StringUtils.isEmpty(modcon)) {
                modcon = "S001M004";
            }
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_CONTRATOS_POR_PAGAR, new Object[]{sociedad, numeroDeudor, modcon});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET
    @Path("contratos/{documentoVenta}/cuotas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarCuotasPorContrato(
            @PathParam("documentoVenta") String documentoVenta,
            @QueryParam("estadoCuota") String estadoCuota
    ) {
        try {
            if (StringUtils.isEmpty(estadoCuota)) {
                estadoCuota = "";
            }
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_CONTRATOS_CUOTAS, new Object[]{documentoVenta, estadoCuota});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET
    @Path("reservas/{documentoVenta}/validar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarCuotasPorReserva(
            @PathParam("documentoVenta") String documentoVenta,
            @QueryParam("importe") String importe
    ) {
        try {

            if (StringUtils.isEmpty(importe)) {
                throw new GELException(CodeError.GEL30024);
            }

            BigDecimal value = BigDecimal.ZERO;
            if (!StringUtils.isEmpty(importe)) {
                value = new BigDecimal(importe);
            }

            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.rawQuery("SELECT NOVAGEL.ZF_GEL_VALIDAR_RESERVA ('" + documentoVenta + "',?) AS success FROM DUMMY", new Object[]{value});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.singleResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET
    @Path("contratos/{documentoVenta}/validar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarCuotasPorContrato(
            @PathParam("documentoVenta") String documentoVenta,
            @QueryParam("numeroCuota") String numeroCuota,
            @QueryParam("importe") String importe
    ) {
        try {
            if (!StringUtils.isEmpty(numeroCuota) && StringUtils.isEmpty(importe)) {
                throw new GELException(CodeError.GEL30024);
            }

            if (!StringUtils.isEmpty(importe) && StringUtils.isEmpty(numeroCuota)) {
                throw new GELException(CodeError.GEL30025);
            }
            if (StringUtils.isEmpty(numeroCuota)) {
                numeroCuota = "";
            } else {
                numeroCuota = "" + Integer.parseInt(numeroCuota);
            }
            BigDecimal value = BigDecimal.ZERO;
            if (!StringUtils.isEmpty(importe)) {
                value = new BigDecimal(importe);
            }

            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.rawQuery("SELECT NOVAGEL.ZF_GEL_VALIDAR_CONTRATO('" + documentoVenta + "','" + numeroCuota + "',?) AS success FROM DUMMY", new Object[]{value});
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.singleResultSet(rs))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @POST
    @Path("reservas/tigomoney")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response tigomoneyReserva(PagoDTO dto) {
        return tigoMoney(dto, HelperSAP.RESERVA);
    }

    @POST
    @Path("contratos/tigomoney")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response tigomoneyContrato(PagoDTO dto) {
        return tigoMoney(dto, HelperSAP.CONTRATO);
    }

    @POST
    @Path("reservas/linkser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response linkserReserva(PagoDTO dto) {
        return linkser(dto, HelperSAP.RESERVA);
    }

    @POST
    @Path("contratos/linkser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response linkserContrato(PagoDTO dto) {
        return linkser(dto, HelperSAP.CONTRATO);
    }

    private Response tigoMoney(PagoDTO dto, String tipoDoc) {
        try {
            HelperSAP.validarDTO(dto);
            i("TIGOMONEY: " + dto.toString());
            String user = "";
            String bank = "";
            dao.openDatabase(configuration.getProperties());
            daoPG.openDatabase(configuration.getProperties());
            ResultSet r = dao.rawQuery("SELECT DISTINCT bukrs,hbkid,usnam FROM SAPABAP1.ZFI_BANK_PRO_WS WHERE codpro = '" + dto.getMatnr() + "' and serban = 'TIG'", null);
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

            r = daoPG.rawQuery("SELECT dolar_type_change FROM seguridad.seg_proyecto WHERE codigo = '" + dto.getMatnr().substring(dto.getMatnr().length() - 3) + "' ", null);
            BigDecimal tc = BigDecimal.ONE;
            try {
                if (r.next()) {
                    tc = r.getBigDecimal("dolar_type_change");
                }
                i("TC: " + tc);
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
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
            ServicioTIGOMONEY serviceT = new ServicioTIGOMONEY(configuration.getProperties(), dto.getBukrs(), tc);
            Map<String, String> responsePagarBs = serviceT.pagarBs(dto, tipoDoc);
            String transaccion = responsePagarBs.get("transaccion");
            i0.getItem().forEach((elem) -> {
                elem.setNumcom(transaccion);
            });
            BigDecimal monto = serviceT.obtenerMonto();
            ZfiWsCobranzasEcTt r1 = serviceS.cobrar(i0);
            if (serviceS.hasErrorCobro(r1)) {
                serviceT.anular(tipoDoc + "#" + dto.toOrderId());
                throw new GELException(CodeError.GEL30021);
            }
            asicon = insertar(r1, transaccion, monto, "BOB");
            return Response.ok(HelperSAP.createResponse(r1, asicon)).build();

        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
            dao.closeDatabase();
        }
    }

    private Response linkser(PagoDTO dto, String tipoDoc) {
        try {
            HelperSAP.validarDTO(dto);
            i("LINKSER: " + dto.toString());
            String user = "";
            String bank = "";
            String codComercio = "";
            dao.openDatabase(configuration.getProperties());
            ResultSet r = dao.rawQuery("SELECT DISTINCT bukrs,hbkid,usnam FROM SAPABAP1.ZFI_BANK_PRO_WS WHERE codpro = '" + dto.getMatnr() + "' and serban = 'LIN'", null);
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
            r = dao.rawQuery("SELECT DISTINCT COM_LINKSER FROM SAPABAP1.ZSD_PROYECTO WHERE PROYECTO = '" + dto.getMatnr() + "'", null);
            try {
                if (r.next()) {
                    codComercio = r.getString("COM_LINKSER");
                }
                i("COD_COMERCIO: " + codComercio);
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            if (codComercio.isEmpty()) {
                throw new GELException(CodeError.GEL30023);
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

            ServicioLINKSER serviceL = new ServicioLINKSER(configuration.getProperties(), dto.getBukrs());
            String transaccion = serviceL.pagar(dto, codComercio);
            i0.getItem().forEach((elem) -> {
                elem.setNumcom(transaccion);
            });
            ZfiWsCobranzasEcTt r1 = serviceS.cobrar(i0);
            if (serviceS.hasErrorCobro(r1)) {
                serviceL.anular();
                throw new GELException(CodeError.GEL30021);
            }
            asicon = insertar(r1, transaccion, BigDecimal.ZERO, "BOB");
            return Response.ok(HelperSAP.createResponse(r1, asicon)).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/clientes/{numeroDeudor}/contratos-sin-entrega")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response contratosSinActaEntrega(
            @PathParam("sociedad") String sociedad,
            @PathParam("numeroDeudor") String numeroDeudor) {
        try {
            List<HashMap<String, Object>> result = new ArrayList<>();
            dao.openDatabase(configuration.getProperties());
            daoPG.openDatabase(configuration.getProperties());
            try {
                ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_CONTRATOS_ACTIVOS, new Object[]{sociedad, numeroDeudor});
                while (rs.next()) {
                    int proy = rs.getInt("proyecto");
                    String uv = rs.getString("nivel1");
                    String mz = rs.getString("nivel2");
                    String lt = rs.getString("nivel3");
                    ResultSet rsx = daoPG.rawQuery("SELECT public.validar_acta_entrega(" + proy + ",'" + uv + "','" + mz + "','" + lt + "')", null);
                    if (rsx.next()) {
                        if (!rsx.getBoolean(1)) {
                            HashMap<String, Object> item = new HashMap<>();

                            item.put("vbeln", rs.getString("contrato"));
                            item.put("matnr", Integer.toString(proy));
                            item.put("maktx", rs.getString("nombre_proyecto"));
                            item.put("atwrt01", uv);
                            item.put("atwrt02", mz);
                            item.put("atwrt03", lt);
                            item.put("stat", rs.getString("estado"));
                            item.put("statx", rs.getString("estadodesc"));
                            result.add(item);
                        }
                    }
                }

            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            return Response.ok(result).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
            dao.closeDatabase();
        }
    }

    @GET
    @Path("voucher/{documentoVenta}-{numeroDocumento}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM})
    public Response voucher(
            @PathParam("documentoVenta") String documentoVenta,
            @PathParam("numeroDocumento") String numeroDocumento) {
        try {
            //List<HashMap<String, Object>> result = new ArrayList<>();
            daoPG.openDatabase(configuration.getProperties());
            dao.openDatabase(configuration.getProperties());
            HashMap<String, Object> item = null;
            try {
                ResultSet rs = daoPG.rawQuery("SELECT asicon,tipdoc FROM pagos.transaccion_response WHERE doccon = '" + documentoVenta + "' AND asicon_sap = '" + numeroDocumento + "';", null);
                if (rs.next()) {
                    asicon = rs.getString("asicon");
                    String tipdoc = rs.getString("tipdoc");
                    if ("C".equals(tipdoc)) {
                        rs = dao.ejecutarConsulta(SPQuery.VOUCHER_CONTRATO, new Object[]{documentoVenta, 0, asicon});
                    } else {
                        rs = dao.ejecutarConsulta(SPQuery.VOUCHER_RESERVA, new Object[]{documentoVenta, asicon});
                    }
                    String moneda = "BOB";
                    if (rs.next()) {
                        item = new HashMap<>();
                        item.put("vbeln", rs.getString("vbln"));
                        //item.put("fplt", rs.getInt("fplt"));
                        item.put("asicon", rs.getString("asicon"));
                        item.put("fkpg", rs.getString("fkpg"));
                        //item.put("fakw", rs.getBigDecimal("fakw"));
                        item.put("name", rs.getString("name").trim());
                        item.put("strSuppl", rs.getString("str_suppl"));
                        item.put("matnr", rs.getString("matnr"));
                        item.put("atwrt01", rs.getString("atwrt_01"));
                        item.put("atwrt02", rs.getString("atwrt_02"));
                        item.put("atwrt03", rs.getString("atwrt_03"));
                        item.put("netwr", rs.getBigDecimal("netwr"));
                        item.put("pname", rs.getString("pname"));
                        item.put("netwr", rs.getBigDecimal("netwr"));
                        moneda = rs.getString("waerk");
                        item.put("waerk", moneda);
                        item.put("cttel", rs.getString("cttel"));
                        item.put("fecven", "C".equals(tipdoc) ? "1900-01-01" : rs.getString("fecven"));
                    }
                    if (item != null) {
                        rs = daoPG.rawQuery("SELECT seqcuo, impcuo FROM pagos.transaccion_response WHERE doccon = '" + documentoVenta + "' AND asicon = '" + asicon + "' ORDER BY 1;", null);
                        String seqcuo = "";
                        BigDecimal total = BigDecimal.ZERO;
                        if (rs.next()) {
                            seqcuo = rs.getString("seqcuo");
                            total = total.add(rs.getBigDecimal("impcuo"));

                        }
                        String seqcuoUlt = "";
                        while (rs.next()) {
                            seqcuoUlt = "-" + rs.getBigDecimal("seqcuo").intValue();
                            total = total.add(rs.getBigDecimal("impcuo"));
                        }
                        item.put("fplt", seqcuo + seqcuoUlt);
                        item.put("fakw", total);
                        item.put("fakwlit", SAPUtils.literalMoneda(total, moneda));
                    }
                } else {
                    String seqcuo = "";
                    BigDecimal total = BigDecimal.ZERO;
                    String moneda = "BOB";
                    if (documentoVenta.charAt(0) == '4') {
                        rs = dao.ejecutarConsulta(SPQuery.VOUCHER_CONTRATO, new Object[]{documentoVenta, 0, numeroDocumento});
                    } else {
                        rs = dao.ejecutarConsulta(SPQuery.VOUCHER_RESERVA, new Object[]{documentoVenta, numeroDocumento});
                    }
                    if (rs.next()) {
                        item = new HashMap<>();
                        item.put("vbeln", rs.getString("vbln"));
                        seqcuo = rs.getString("fplt");
                        item.put("asicon", rs.getString("asicon"));
                        item.put("fkpg", rs.getString("fkpg"));
                        total = total.add(rs.getBigDecimal("fakw"));
                        item.put("name", rs.getString("name").trim());
                        item.put("strSuppl", rs.getString("str_suppl"));
                        item.put("matnr", rs.getString("matnr"));
                        item.put("atwrt01", rs.getString("atwrt_01"));
                        item.put("atwrt02", rs.getString("atwrt_02"));
                        item.put("atwrt03", rs.getString("atwrt_03"));
                        item.put("netwr", rs.getBigDecimal("netwr"));
                        item.put("pname", rs.getString("pname"));
                        moneda = rs.getString("waerk");
                        item.put("waerk", moneda);
                        item.put("cttel", rs.getString("cttel"));
                        item.put("fecven", documentoVenta.charAt(0) == '4' ? "1900-01-01" : rs.getString("fecven"));
                    }
                    if (item != null) {
                        String seqcuoUlt = "";
                        while (rs.next()) {
                            seqcuoUlt = " - " + rs.getBigDecimal("fplt").intValue();
                            total = total.add(rs.getBigDecimal("fakw"));
                            item.put("netwr", rs.getBigDecimal("netwr"));
                        }
                        item.put("fplt", seqcuo + seqcuoUlt);
                        item.put("fakw", total);
                        item.put("fakwlit", SAPUtils.literalMoneda(total, moneda));
                    }

                }
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            return Response.ok(item).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
            dao.closeDatabase();
        }

    }

    @GET
    @Path("formas-pagos/{matnr}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response formasDePagos(@PathParam("matnr") String matnr) {
        try {
            matnr = matnr.substring(matnr.length() - 3);
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.FORMAS_DE_PAGO, new Object[]{matnr});
            HashMap<Integer, String> nombres = new HashMap<>();
            nombres.put(4, "tipoPago");
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.listResultSet(rs, nombres))).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }

    }

    @GET
    @Path("simulador/{matnr}/lotes/{lote}/tipo-venta/{tipoVenta}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response simulador(@PathParam("matnr") String matnr,
            @PathParam("lote") String lote,
            @PathParam("tipoVenta") String tipoVenta,
            @QueryParam("porcentaje") BigDecimal porcentaje,
            @QueryParam("cuotaInicial") BigDecimal cuotaInicial,
            @QueryParam("plazos") Integer plazos,
            @QueryParam("fecha") String fecha
    ) {
        try {
            if (StringUtils.isEmpty(matnr)) {

            }
            matnr = matnr.substring(matnr.length() - 3);
            if (StringUtils.isEmpty(lote)) {

            }
            if (StringUtils.isEmpty(tipoVenta)) {

            }

            dao.openDatabase(configuration.getProperties());
            ResultSet rs;
            HashMap<String, Object> header = null;
            if ("Z001".equals(tipoVenta)) {
                rs = dao.ejecutarConsulta(SPQuery.SIMULADOR_ALCONTADO, new Object[]{matnr, lote});
                try {
                    if (rs.next()) {
                        header = new HashMap<>();
                        header.put("precioLista", rs.getBigDecimal("precio_lista"));
                        header.put("precioDescuento", rs.getBigDecimal("descuento"));
                        header.put("precioVenta", rs.getBigDecimal("nuevo_precio_venta"));

                        header.put("descuento", rs.getBigDecimal("porcentaje"));
                        header.put("precioListaAjustado", rs.getBigDecimal("precio_lista_ajuste"));
                        header.put("totalPlanPago", BigDecimal.ZERO);
                    }
                } catch (SQLException ex) {
                    throw new GELException(CodeError.GEL20000, ex);
                }
            } else {
                rs = dao.ejecutarConsulta(SPQuery.SIMULADOR_APLAZOS_HEADER, new Object[]{matnr, lote, porcentaje, cuotaInicial, plazos});
                try {
                    String moneda = "";
                    BigDecimal cuota = BigDecimal.ZERO;
                    BigDecimal totalPlanpago = BigDecimal.ZERO;
                    if (rs.next()) {
                        header = new HashMap<>();
                        header.put("precioLista", rs.getBigDecimal("precio_lista"));
                        header.put("precioDescuento", rs.getBigDecimal("descuento"));
                        header.put("precioVenta", rs.getBigDecimal("nuevo_precio_venta"));

                        header.put("descuento", porcentaje);
                        header.put("precioListaAjustado", rs.getBigDecimal("precio_lista_ajuste"));

                        moneda = rs.getString("moneda");
                        cuota = rs.getBigDecimal("monto_cuota");
                        totalPlanpago = rs.getBigDecimal("nuevo_total_pp");
                        header.put("totalPlanPago", totalPlanpago);
                    }
                    if (cuota.doubleValue() <= 0) {
                        throw new GELException(CodeError.GEL30026);
                    }
                    if (header != null) {
                        rs.close();
                        List<HashMap<String, Object>> lista = new ArrayList<>();
                        for (int numeroCuota = 1; numeroCuota <= plazos; numeroCuota++) {
                            rs = dao.ejecutarConsulta(SPQuery.SIMULADOR_APLAZOS_DETAIL, new Object[]{numeroCuota, fecha, cuota, moneda, totalPlanpago});
                            if (rs.next()) {
                                HashMap<String, Object> item = new HashMap<>();
                                item.put("numeroCuota", rs.getString("z_nro_cuota"));
                                item.put("fecha", rs.getString("z_fecha"));
                                item.put("valorCuota", rs.getBigDecimal("z_valor_cuota"));
                                item.put("moneda", rs.getString("z_moneda"));
                                item.put("saldo", rs.getBigDecimal("z_total"));
                                lista.add(item);
                            }
                        }
                        header.put("cuotas", lista);
                    }
                } catch (SQLException ex) {
                    throw new GELException(CodeError.GEL20000, ex);
                }
            }
            return Response.ok(header).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }

    }

    @GET
    @Path("tipo-cambio/{matnr}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response tipoCambio(@PathParam("matnr") String matnr) {
        try {
            daoPG.openDatabase(configuration.getProperties());
            HashMap<String, Object> result = new HashMap<>();
            ResultSet r = daoPG.rawQuery("SELECT dolar_type_change FROM seguridad.seg_proyecto WHERE codigo = '" + matnr.substring(matnr.length() - 3) + "' ", null);
            BigDecimal tc = BigDecimal.ONE;
            try {
                if (r.next()) {
                    tc = r.getBigDecimal("dolar_type_change");
                }
                result.put("tcTigomoney", tc);
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            result.put("tc", new BigDecimal("6.96"));
            return Response.ok(result).build();

        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    private String insertar(ZfiWsCobranzasEcTt r1, String transaccion, BigDecimal monto, String moneda) {
        try {
            daoPG.openDatabase(configuration.getProperties());
            asicon = "";
            final List<ZfiWsCobranzasEcStr> lista = r1.getItem();
            for (int i = lista.size(); i > 0; i--) {
                ZfiWsCobranzasEcStr elem = lista.get(i - 1);
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

    @GET
    @Path("mapas/{matnr}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response listarWms(
            @PathParam("matnr") String matnr) {
        try {
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery("SELECT * FROM pre_reserva.fc_wms_parametro (" + Integer.parseInt(matnr) + ")", null);
            try {
                if (rs.next()) {

                    HashMap<String, Object> item = new HashMap<>();
                    item.put("matnr", matnr);
                    item.put("urlBase", "https://wms.grupo-lafuente.com/cgi-bin/" + rs.getString("url_base") + "/qgis_mapserv.fcgi?");
                    item.put("lyBase", rs.getString("capa_base"));
                    item.put("urlLote", "https://wms.grupo-lafuente.com/cgi-bin/" + rs.getString("url_lote") + "/qgis_mapserv.fcgi?");
                    item.put("lyLote", rs.getString("capa_lote"));
                    item.put("lat", rs.getBigDecimal("latitud"));
                    item.put("lng", rs.getBigDecimal("longitud"));
                    item.put("zoom", rs.getString("zoom"));
                    return Response.ok(item).build();
                } else {
                    throw new GELException("WMS001", "No existe WMS para el proy: " + matnr);
                }
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }

        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/proyectos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response listarProyectos(
            @PathParam("sociedad") String sociedad) {
        List<HashMap<String, Object>> result = new ArrayList<>();
        try {
            dao.openDatabase(configuration.getProperties());
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = dao.rawQuery("SELECT DISTINCT PROYECTO, DESCRIPCION, PROPIETARIO, MONEDA, MINIMO, COMERCIALIZA, MONTO_RESERVA FROM SAPABAP1.ZSD_PROYECTO WHERE COMERCIALIZA = '" + sociedad + "' ORDER BY PROYECTO LIMIT 32", null);
            String matnr = "";
            String maktx = "";
            String bukrs = "";
            String waerk = "";
            BigDecimal minimo = BigDecimal.ZERO;
            String comercializa = "";
            BigDecimal montoReserva = BigDecimal.ZERO;
            try {
                while (rs.next()) {
                    matnr = rs.getString("PROYECTO");
                    maktx = rs.getString("DESCRIPCION");
                    bukrs = rs.getString("PROPIETARIO");
                    waerk = rs.getString("MONEDA");
                    minimo = rs.getBigDecimal("MINIMO");
                    comercializa = rs.getString("COMERCIALIZA");
                    montoReserva = rs.getBigDecimal("MONTO_RESERVA");
                    ResultSet rsImg = daoPG.rawQuery("SELECT url_imagen_web FROM seguridad.seg_proyecto WHERE codigo = '" + Integer.parseInt(matnr) + "' ", null);
                    if (rsImg.next()) {
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("matnr", matnr);
                        item.put("maktx", maktx);
                        item.put("bukrs", bukrs);
                        item.put("bukrsCom", comercializa);
                        item.put("min", minimo);
                        item.put("waerk", waerk);
                        item.put("montoReseva", montoReserva);
                        item.put("urlImg", rsImg.getString("url_imagen_web"));
                        result.add(item);
                    }
                }
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }

            return Response.ok(result).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            System.out.println("FIN DATABASE");
            daoPG.closeDatabase();
            //dao.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/proyectos/{matnr}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response listarProyectos(
            @PathParam("sociedad") String sociedad,
            @PathParam("matnr") String matnr) {
        try {
            dao.openDatabase(configuration.getProperties());
            daoPG.openDatabase(configuration.getProperties());
            matnr = matnr.substring(matnr.length() - 3);
            ResultSet rs = dao.rawQuery("SELECT DISTINCT PROYECTO, DESCRIPCION, PROPIETARIO, MONEDA, MINIMO, COMERCIALIZA, MONTO_RESERVA FROM SAPABAP1.ZSD_PROYECTO WHERE COMERCIALIZA = '" + sociedad + "' AND CAST(PROYECTO AS INTEGER) = " + matnr + " ORDER BY PROYECTO ", null);
            String proy = "";
            String maktx = "";
            String bukrs = "";
            String waerk = "";
            BigDecimal minimo = BigDecimal.ZERO;
            String comercializa = "";
            BigDecimal montoReserva = BigDecimal.ZERO;
            HashMap<String, Object> item = null;
            try {

                while (rs.next()) {
                    proy = rs.getString("PROYECTO");
                    maktx = rs.getString("DESCRIPCION");
                    bukrs = rs.getString("PROPIETARIO");
                    waerk = rs.getString("MONEDA");
                    minimo = rs.getBigDecimal("MINIMO");
                    comercializa = rs.getString("COMERCIALIZA");
                    montoReserva = rs.getBigDecimal("MONTO_RESERVA");
                    ResultSet rsImg = daoPG.rawQuery("SELECT url_imagen_web FROM seguridad.seg_proyecto WHERE codigo = '" + Integer.parseInt(proy) + "' ", null);
                    if (rsImg.next()) {
                        item = new HashMap<>();
                        item.put("matnr", proy);
                        item.put("maktx", maktx);
                        item.put("bukrs", bukrs);
                        item.put("bukrsCom", comercializa);
                        item.put("min", minimo);
                        item.put("waerk", waerk);
                        item.put("montoReseva", montoReserva);
                        item.put("urlImg", rsImg.getString("url_imagen_web"));

                    }
                }
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }

            return Response.ok(item).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
            dao.closeDatabase();
        }
    }

    @GET
    @Path("lotes/{matnr}/por-ubicacion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarLotes(
            @PathParam("matnr") String matnr,
            @QueryParam("latitud") String latitud,
            @QueryParam("longitud") String longitud
    ) {
        try {
            matnr = matnr.substring(matnr.length() - 3);
            String query = "SELECT cod_lote FROM public.fc_wms_lotes_comercial ('" + matnr + "','" + longitud + "','" + latitud + "')";
            daoMap.openDatabase(configuration.getProperties());
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = daoMap.rawQuery(query, null);
            try {
                if (rs.next()) {
                    String[] lote = rs.getString("cod_lote").split("-");
                    ResultSet rsSap = dao.ejecutarConsulta(SPQuery.GET_LOTE, new Object[]{Integer.parseInt(lote[0]), lote[1] + lote[2] + lote[3]});

                    return Response.ok(new ArrayOutput(ResultSetToJsonMapper.singleResultSet(rsSap))).build();
                }

            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            throw new GELException("WMS002", "No existe registro.");
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
            daoMap.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/clientes/{numeroDeudor}/tiene-reservas-contratos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarReservasContratos(
            @PathParam("sociedad") String sociedad,
            @PathParam("numeroDeudor") String numeroDeudor,
            @QueryParam("modcon") String modcon,
            @QueryParam("modres") String modres
    ) {
        try {
            if (StringUtils.isEmpty(modres)) {
                modres = "S001M003";
            }
            if (StringUtils.isEmpty(modcon)) {
                modcon = "S001M004";
            }
            HashMap<String, Object> resultado = new HashMap<>();
            resultado.put("partner", numeroDeudor);
            dao.openDatabase(configuration.getProperties());
            ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_CONTRATOS_POR_PAGAR, new Object[]{sociedad, numeroDeudor, modcon});
            try {
                if (rs.next()) {
                    resultado.put("deudas", true);
                    return Response.ok(resultado).build();
                }
                rs = dao.ejecutarConsulta(SPQuery.LIST_RESERVAS_POR_PAGAR, new Object[]{sociedad, numeroDeudor, modres});
                if (rs.next()) {
                    resultado.put("deudas", true);
                    return Response.ok(resultado).build();
                }
                resultado.put("deudas", false);
                return Response.ok(resultado).build();
            } catch (SQLException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }

        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET
    @Path("{sociedad}/mensajes/{tipo}/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listar(
            @PathParam("sociedad") String sociedad,
            @PathParam("tipo") String tipo,
            @PathParam("id") String id
    ) {
        try {
            //HashMap<String, Object> resultado = new HashMap<>();
            //resultado.put("partner", numeroDeudor);
            daoPG.openDatabase(configuration.getProperties());
            ResultSet rs = daoPG.rawQuery("SELECT * FROM seguridad.zf_gel_s0001f9('" + sociedad + "','" + tipo + "','" + id + "')", null);
            return Response.ok(new ArrayOutput(ResultSetToJsonMapper.singleResultSet(rs))).build();

        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            daoPG.closeDatabase();
        }
    }

    @POST
    @Path("{sociedad}/solicitud-compra")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveSolicitud(
            @PathParam("sociedad") String sociedad, SolicitudCompraDTO solicitud
    ) {
        
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("numeroSolicitud", "7025632891");
        return Response.ok(result).build();
    }
    //SELECT * FROM seguridad.zf_gel_s0001f9(    in_sociedad character varying,    in_tipo character varying,    in_codigo character varying)

    private void i(String message) {
        if (!StringUtils.isEmpty(message)) {
            Logger.getLogger("SD")
                    .log(Level.INFO, message);
        }
    }
}
