/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.tigomoney.ws;

import bo.com.vlink.services.CustomerServices;
import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.rest.dto.PagoDTO;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

/**
 *
 * @author guido
 */
public class ServicioTIGOMONEY {

    private final String DOLAR = "USD";
    private CustomerServices port;
    private final Map<String, Object> config;
    private final String bukrs;
    private final BigDecimal cambio;
    public static final String OK = "0";
    private BigDecimal monto = null;

    public ServicioTIGOMONEY(Map<String, Object> config, String bukrs, BigDecimal cambio) {
        port = null;
        this.config = config;
        this.bukrs = bukrs;
        this.cambio = cambio;
    }

    public Map<String, String> pagarBs(PagoDTO dto, String tipoDoc) throws GELException {
        try {
            port = crearServicio();
            String key = getllaveIdentificacion();
            String parametros = getParametros(dto, tipoDoc);
            String result = port.solicitarPago(key, parametros);

            Map<String, String> resultMap = createMapa(result);
            String codeRes = resultMap.get("codRes").trim();
            if (!OK.equals(codeRes)) {
                throw new GELException(codeRes, resultMap.get("mensaje"));
            }
            return resultMap;
        } catch (javax.xml.ws.WebServiceException ex) {
            throw new GELException(CodeError.GEL30050, ex);
        }

    }

    public Map<String, String> anular(String orderId) throws GELException {

        try {
            port = crearServicio();
            String key = getllaveIdentificacion();
            String llaveEncriptacion = (String) config.get("bukrs." + bukrs + ".encrypt");
            byte[] encritpado2 = CTripleDes.encrypt("pv_orderId=" + orderId, llaveEncriptacion);
            String parametros = new String(CBase64.encode(encritpado2));
            String result = port.revertirPago(key, parametros);
            return createMapa(result);
        } catch (javax.xml.ws.WebServiceException ex) {
            throw new GELException(CodeError.GEL30050, ex);
        } catch (Exception ex) {
            throw new GELException(ex);
        }

    }

    private CustomerServices crearServicio() {
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
            factory.setAddress((String) config.get("ws.tigomoney.url"));
            port = factory.create(CustomerServices.class);
            ClientImpl client = (ClientImpl) ClientProxy.getClient(port);

            LoggingInInterceptor inInterceptor = new LoggingInInterceptor();
            inInterceptor.setPrettyLogging(true);
            client.getInInterceptors().add(inInterceptor);

            LoggingOutInterceptor outInterceptor = new LoggingOutInterceptor();
            outInterceptor.setPrettyLogging(true);
            client.getOutInterceptors().add(outInterceptor);
            
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(50000L);
            policy.setReceiveTimeout(50000L);
            conduit.setClient(policy);
            

            HTTPConduit http = (HTTPConduit) client.getConduit();
            TLSClientParameters tlsCP = new TLSClientParameters();
            tlsCP.setUseHttpsURLConnectionDefaultSslSocketFactory(true);
            tlsCP.setUseHttpsURLConnectionDefaultHostnameVerifier(true);
            http.setTlsClientParameters(tlsCP);

        }
        return port;
    }

    private String getllaveIdentificacion() {
        return (String) config.get("bukrs." + bukrs + ".key");
    }

    public BigDecimal obtenerMonto() {
        return monto;
    }

    private String getParametros(PagoDTO dto, String tipoDoc) throws GELException {
        try {
            monto = new BigDecimal(dto.getMonto().doubleValue());
            if (DOLAR.equalsIgnoreCase(dto.getMoneda())) {
                monto = monto.multiply(cambio);
            }
            monto = setScale(2, monto);
            String llaveEncriptacion = (String) config.get("bukrs." + bukrs + ".encrypt");
            String confirmacion = (String) config.get("bukrs." + bukrs + ".confirm");
            String parametro = "";
            parametro += "pv_nroDocumento=;";
            parametro += "pv_linea=" + dto.getLinea() + ";";
            parametro += "pv_monto=" + monto + ";";
            parametro += "pv_orderId=" + tipoDoc + "#" + dto.toOrderId() + ";";
            parametro += "pv_mensaje=" + dto.toMensaje() + ";";
            parametro += "pv_nombre=;";
            parametro += "pv_confirmacion=" + confirmacion + ";";
            parametro += "pv_notificacion=" + confirmacion + ";";
            parametro += "pv_urlCorrecto=;";
            parametro += "pv_urlError=;";
            parametro += "pv_items=;";
            parametro += "pv_razonSocial=;";
            parametro += "pv_nit=";
            return new String(CBase64.encode(CTripleDes.encrypt(parametro, llaveEncriptacion)));
        } catch (Exception ex) {
            throw new GELException(ex);
        }
    }

    public BigDecimal setScale(int scale, BigDecimal valor) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(otherSymbols);
        df.setMaximumFractionDigits(scale);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);
        return new BigDecimal(df.format(valor));
    }

    private Map<String, String> createMapa(String result) {
        try {
            String llaveEncriptacion = (String) config.get("bukrs." + bukrs + ".encrypt");
            String resultado = CTripleDes.decrypt(CBase64.decode(result.toCharArray()), llaveEncriptacion);
            Map<String, String> map = new HashMap<>();
            String[] pairs = resultado.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 1) {
                    map.put(keyValue[0], "");
                } else if (keyValue.length == 2) {
                    map.put(keyValue[0], keyValue[1]);
                }
            }
            return map;
        } catch (Exception ex) {
            return null;
        }
    }

}
