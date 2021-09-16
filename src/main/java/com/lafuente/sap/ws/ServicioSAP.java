package com.lafuente.sap.ws;

import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.rest.dto.PagoDTO;
import com.sap.document.sap.soap.functions.mc_style.TmsSVbelnRange;
import com.sap.document.sap.soap.functions.mc_style.TmsTVbelnRange;
import com.sap.document.sap.soap.functions.mc_style.ZFIWSCOBRANZA;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobrConsCuotasETt;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasEaTt;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasEcStr;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasEcTt;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasIaTt;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasIcTt;
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
 * @author guido
 */
public class ServicioSAP {

    private ZFIWSCOBRANZA port;
    private final Map<String, Object> config;
    private final String bank;
    private final String user;

    public ServicioSAP(Map<String, Object> config, String bank, String user) {
        port = null;
        this.config = config;
        this.bank = bank;
        this.user = user;
    }

    public ZfiWsCobrConsCuotasETt obtenerCuotas(PagoDTO dto, boolean isReserva) throws GELException {
        try {
            int mes = dto.getItems().size();
            if (isReserva) {
                mes = 999;
            }
            TmsTVbelnRange iVbeln = new TmsTVbelnRange();
            iVbeln.getItem().add(new TmsSVbelnRange());
            iVbeln.getItem().get(0).setSign("I");
            iVbeln.getItem().get(0).setOption("EQ");
            iVbeln.getItem().get(0).setLow(dto.getDocven());
            iVbeln.getItem().get(0).setHigh(dto.getDocven());
            port = crearServicio();
            return port.zfiWsCobranzasConsCuotas(bank, mes, null, null, iVbeln);
        } catch (javax.xml.ws.WebServiceException ex) {
            throw new GELException(CodeError.GEL30070, ex);
        }

    }

    public ZfiWsCobranzasEcTt cobrar(ZfiWsCobranzasIcTt i0) throws GELException {
        try {
            port = crearServicio();
            return port.zfiWsCobranzasCobranza(i0);
        } catch (javax.xml.ws.WebServiceException ex) {
            return null;
            //throw new GELException(CodeError.GEL30070, ex);
        }
    }

    private ZFIWSCOBRANZA crearServicio() {
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
            factory.setAddress((String) config.get("ws.sap.url"));
            port = factory.create(ZFIWSCOBRANZA.class);
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

    public boolean hasErrorCobro(ZfiWsCobranzasEcTt r1) {
        if (r1 == null) {
            return true;
        }
        for (ZfiWsCobranzasEcStr elem : r1.getItem()) {
            if (!"01".equalsIgnoreCase(elem.getResul())) {
                return true;
            }
        }
        return false;
    }

    public ZfiWsCobranzasEaTt anular(ZfiWsCobranzasIaTt i0) {
       try {
            port = crearServicio();
            return port.zfiWsCobranzasAnulacion(i0);
        } catch (javax.xml.ws.WebServiceException ex) {
            return null;
            //throw new GELException(CodeError.GEL30070, ex);
        }

    }

}
