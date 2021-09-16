/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest;

import com.lafuente.sap.dao.PGDatabase;
import com.lafuente.sap.dao.SPDao;
import com.lafuente.sap.dao.SPQuery;
import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.exceptions.GELExceptionMapping;
import com.lafuente.sap.utils.CryptoAppUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author dcaceres
 */
@Path("job")
@Stateless
public class JobEndpoint {

    private final String RESPONSE_SMS = "Response: SuccessMessage: Commit successfully!";
    @Inject
    private SPDao dao;
    @Context
    private Configuration configuration;
    @Inject
    private PGDatabase daoPG;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarTicket() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            dao.openDatabase(configuration.getProperties());
            daoPG.openDatabase(configuration.getProperties());
            ResultSet r = daoPG.rawQuery("SELECT * FROM sap.lista_premiados();", null);
            try {
                while (r.next()) {
                    String numeroDeudor = r.getString("codigo");
                    ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_CLIENTES, new Object[]{"1600", numeroDeudor, ""});
                    if (rs.next()) {
                        String usuario = r.getString("usuario");
                        String password = r.getString("password");
                        String telefono = rs.getString("tel_number1");
                        String content = r.getString("content");
                        String url = r.getString("url_sms");
                        String ticket = r.getString("numero_ticket");

                        telefono = StringUtils.defaultString(telefono).trim();

                        url += "account=" + CryptoAppUtils.desencriptar(usuario);
                        url += "&password=" + CryptoAppUtils.desencriptar(password);
                        url += "&port=" + r.getString("port");
                        url += "&destination=" + telefono;
                        url += "&content=" + URLEncoder.encode(content, StandardCharsets.UTF_8.toString());

                        if (telefono.length() == 11 && (telefono.startsWith("5917") || telefono.startsWith("5916"))) {
                            String responseSMS = sendGet(url);
                            if (RESPONSE_SMS.equals(responseSMS)) {
                                //i("SMS Enviado: " + telefono);
                                Map<String, Object> values = new HashMap<>();
                                values.put("fech_envio", new Date());
                                values.put("estado", "ENVIADO");
                                values.put("telf_envio", telefono);
                                result.add(values);
                                daoPG.update("sap.sttkpremiados", values, "nro_ticket='" + ticket + "'", null);
                            } else {
                                //i("SMS No enviado: " + telefono);
                                Map<String, Object> values = new HashMap<>();
                                values.put("fech_envio", new Date());
                                values.put("estado", "NO ENVIADO");
                                values.put("telf_envio", telefono);
                                daoPG.update("sap.sttkpremiados", values, "nro_ticket='" + ticket + "'", null);
                            }
                        } else {
                            //i("SMS No enviado: " + telefono);
                            Map<String, Object> values = new HashMap<>();
                            values.put("fech_envio", new Date());
                            values.put("estado", "NUMERO ERROR");
                            values.put("telf_envio", telefono);
                            daoPG.update("sap.sttkpremiados", values, "nro_ticket='" + ticket + "'", null);
                        }

                    } else {
                        String ticket = r.getString("numero_ticket");
                        //i("Codigo no existe: " + numeroDeudor);
                        Map<String, Object> values = new HashMap<>();
                        values.put("fech_envio", new Date());
                        values.put("estado", "ERROR");
                        daoPG.update("sap.sttkpremiados", values, "nro_ticket='" + ticket + "'", null);
                    }
                }
            } catch (SQLException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            return Response.ok(result).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    @GET()
    @Path("/1200")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarTicket1200() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            dao.openDatabase(configuration.getProperties());
            daoPG.openDatabase(configuration.getProperties());
            ResultSet r = daoPG.rawQuery("SELECT * FROM sap.lista_premiados_1200();", null);
            try {
                while (r.next()) {
                    String numeroDeudor = r.getString("codigo");
                    String usuario = r.getString("usuario");
                    String password = r.getString("password");
                    String telefono = r.getString("telefono");//r.getString("tel_number1");
                    String content = r.getString("content");
                    String url = r.getString("url_sms");
                    String ticket = r.getString("numero_ticket");

                    telefono = StringUtils.defaultString(telefono).trim();
                    url += "account=" + CryptoAppUtils.desencriptar(usuario);
                    url += "&password=" + CryptoAppUtils.desencriptar(password);
                    url += "&port=" + r.getString("port");
                    url += "&destination=" + telefono;
                    url += "&content=" + URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
                    if (!enviarSMS("1200", url, telefono, ticket)) {
                        ResultSet rs = dao.ejecutarConsulta(SPQuery.LIST_CLIENTES, new Object[]{"1200", numeroDeudor, ""});
                        telefono = rs.getString("tel_number1");
                        if (rs.next()) {
                            enviarSMS("1200", url, telefono, ticket);
                        }
                    }
                }
            } catch (SQLException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException ex) {
                throw new GELException(CodeError.GEL20000, ex);
            }
            return Response.ok(result).build();
        } catch (GELException ex) {
            throw new GELExceptionMapping(ex);
        } finally {
            dao.closeDatabase();
        }
    }

    private String sendGet(String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 200 || code == 201) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                return sb.toString();
            }
            return null;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private void i(String message) {
        if (!StringUtils.isEmpty(message)) {
            Logger.getLogger("JOB")
                    .log(Level.INFO, message);
        }
    }

    private boolean enviarSMS(String url, String sociedad, String telefono, String ticket) throws GELException {
        if (telefono.length() == 11 && (telefono.startsWith("5917") || telefono.startsWith("5916"))) {
            String responseSMS = sendGet(url);
            if (RESPONSE_SMS.equals(responseSMS)) {
                daoPG.rawQuery("SELECT sap.actualizar_ticket('" + sociedad + "','ENVIADO','" + telefono + "','" + ticket + "') ", null);
                return true;
            } else {
                daoPG.rawQuery("SELECT sap.actualizar_ticket('" + sociedad + "','NO ENVIADO','" + telefono + "','" + ticket + "') ", null);
            }
        } else {
            daoPG.rawQuery("SELECT sap.actualizar_ticket('" + sociedad + "','NUMERO ERROR','" + telefono + "','" + ticket + "') ", null);
        }
        return false;
    }
}
