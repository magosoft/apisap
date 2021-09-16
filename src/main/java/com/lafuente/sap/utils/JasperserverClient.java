/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author dcaceres
 */
public class JasperserverClient {

    //http://172.28.3.53:8080/jasperserver/rest_v2/reports/Varios/ReporteDesarrollo/acta_entrega.html?jsonEncode=
    private final String URI_REPORTS = "jasperserver/rest_v2/reports";
    private final String AUTHORIZATION = "Authorization";
    private final String urlServer = "http://172.28.3.53:8080";
    //private final String pathReport = "Varios/ReporteDesarrollo/acta_entrega";
    private final String EXTENSION_PDF = ".pdf";
    private final String username = "jasperadmin";
    private final String password = "admin123*";
   

    public JasperserverClient() {

    }    

    public InputStream getPdf(String pathReport, HashMap<String, String> queryParam) {
       
        Client client = ClientBuilder.newClient();
        WebTarget target
                = client.target(urlServer)
                        .path(URI_REPORTS)
                        .path(pathReport + EXTENSION_PDF)
                        .property("accept", "application/pdf");
        for (Map.Entry<String, String> entry : queryParam.entrySet()) {
            String value = entry.getValue();
            System.out.println(value);
           target = target.queryParam(entry.getKey(), value);
        }
        
        Response result = target
                .request()
                .header(AUTHORIZATION, token(username, password))
                .get();
        if (result.getStatus() != 200) {
            return null;
        }
        return result.readEntity(InputStream.class);
    }

    private String token(String usuario, String password) {
        String token = usuario + ":" + password;
        return "Basic " + DatatypeConverter.printBase64Binary(token.getBytes(Charset.forName("UTF-8")));
    }
}
