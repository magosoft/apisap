/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.ws;

import com.lafuente.sap.exceptions.GELException;
import com.sap.document.sap.rfc.functions.ZCOMERCIAL;
import com.sap.document.sap.rfc.functions.ZIDRESERVA;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;

/**
 *
 * @author dcaceres
 */
public class ServicioSAPGEL {

    private ZCOMERCIAL port;
    private final Map<String, Object> config;
    
    private final String user;

    public ServicioSAPGEL(Map<String, Object> config,  String user) {
        port = null;
        this.config = config;        
        this.user = user;
    }

    private ZCOMERCIAL crearServicio() {
        if (port == null) {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
                };
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HostnameVerifier allHostsValid = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            } catch (NoSuchAlgorithmException | KeyManagementException ex) {
                System.out.println(ex.getMessage());
            }
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setAddress((String) config.get("ws.sap.url_2"));
            port = factory.create(ZCOMERCIAL.class);
            ClientImpl client = (ClientImpl) ClientProxy.getClient(port);

            LoggingInInterceptor inInterceptor = new LoggingInInterceptor();
            inInterceptor.setPrettyLogging(true);
            client.getInInterceptors().add(inInterceptor);

            LoggingOutInterceptor outInterceptor = new LoggingOutInterceptor();
            outInterceptor.setPrettyLogging(true);
            client.getOutInterceptors().add(outInterceptor);

            HTTPConduit http = (HTTPConduit) client.getConduit();
            TLSClientParameters tlsCP = new TLSClientParameters();
            tlsCP.setUseHttpsURLConnectionDefaultSslSocketFactory(true);
            tlsCP.setUseHttpsURLConnectionDefaultHostnameVerifier(true);
            http.setTlsClientParameters(tlsCP);

            AuthorizationPolicy authPolicy = new AuthorizationPolicy();
            authPolicy.setAuthorizationType("Basic");
            authPolicy.setUserName(user);
            authPolicy.setPassword((String) config.get("ws.sap." + user));
            http.setAuthorization(authPolicy);
        }
        return port;
    }

    public String registrar(String kunnr, String lote, String matnr, BigDecimal netwr, String plazo, String tipoVenta) throws GELException {
        crearServicio();
        ZIDRESERVA result = port.zsdWSCOMERCIALRESERVAR(kunnr, lote, matnr, netwr, plazo, tipoVenta);
        if ("X".equals(result.getSUCCESS())) {
            return result.getVBELN();
        }
        throw new GELException(result.getMESSAGE());
    }
}
