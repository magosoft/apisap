/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.linkser.ws;

import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.rest.dto.PagoDTO;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import org.apache.cxf.transport.common.gzip.GZIPInInterceptor;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import servicio.ServiciosEcommeLNK;

/**
 *
 * @author guido
 */
public class ServicioLINKSER {

    private final String DESCANSO_DEL_EMISOR = "8";
    private final String SWITCH_NO_DISPONIBLE = "91";
    private final String EMISOR_INVALIDO = "92";
    private final String OPERACION_EXITOSA = "0";
    private ServiciosEcommeLNK port;
    private final Map<String, Object> config;
    private final String bukrs;
    public static String BOB = "068";
    public static String USD = "840";

    private final String passwordCifrado;
    private final String institucionCifrado;
    private int fechaEnvio = 19800101;
    private String horaEnvio = "000000";
    private String secuencial = "000001";
    private String reto = "0";
    private String firmaDigital = "";
    private String validacionDigital;

    public ServicioLINKSER(Map<String, Object> config, String bukrs) {
        this.config = config;
        this.bukrs = bukrs;
        institucionCifrado = HelperLINKSER.cifrarPublicaLinkser((String) config.get("ws.linkser.codins"));
        passwordCifrado = HelperLINKSER.cifrarPublicaLinkser((String) config.get("ws.linkser.passwd"));

    }

    public String pagar(PagoDTO dto, String codigoComercio) throws GELException {
        try {
            port = crearServicio();
            String codigoTerminal = (String) config.get("ws.linkser.codter");
            String[] linea = HelperLINKSER.desencriptar(dto.getLinea(), config).split("-");
            String tarjetaCifrado = HelperLINKSER.cifrarPublicaLinkser(linea[0]);
            String expiracionCifrado = HelperLINKSER.cifrarPublicaLinkser(linea[1]);
            String cvvCifrado = HelperLINKSER.cifrarPublicaLinkser(linea[2]);
            fechaEnvio = obtenerFechaDelSistema();
            horaEnvio = obtenerHoraDelSistema();
            String importe = obtenerImporte(dto.getMonto());
            String moneda = "USD".equalsIgnoreCase(dto.getMoneda()) ? USD : BOB;
            secuencial = getSecuencial();
            reto = port.getReto(); 
            firmaDigital = HelperLINKSER.validarFirma((String) config.get("ws.linkser.codins"), reto);
            List<Object> respuesta = port.meSetAuthoEcomm(institucionCifrado, secuencial, codigoComercio, codigoTerminal, tarjetaCifrado, bukrs + "_ECOMME",
                    expiracionCifrado, cvvCifrado, importe, moneda, fechaEnvio, horaEnvio, reto, firmaDigital, passwordCifrado);
            String codigoRespuesta = OPERACION_EXITOSA;
            if (respuesta.size() > 3) {
                codigoRespuesta = toStr(respuesta.get(2));
                codigoRespuesta = codigoRespuesta.trim();
                if (DESCANSO_DEL_EMISOR.equals(codigoRespuesta) || SWITCH_NO_DISPONIBLE.equals(codigoRespuesta) || EMISOR_INVALIDO.equals(codigoRespuesta)) {
                    int fechaTransaccion = fechaEnvio;
                    fechaEnvio = obtenerFechaDelSistema();
                    horaEnvio = obtenerHoraDelSistema();
                    port.meSetReverEcomm(institucionCifrado, secuencial, fechaTransaccion, fechaEnvio, horaEnvio, reto, firmaDigital, passwordCifrado);
                }
                if (!OPERACION_EXITOSA.equals(codigoRespuesta)) {
                    throw new GELException(codigoRespuesta, (String) respuesta.get(3));
                }
            }
            codigoRespuesta = toStr(respuesta.get(0));
            codigoRespuesta = codigoRespuesta.trim();
            if (!OPERACION_EXITOSA.equals(codigoRespuesta)) {
                throw new GELException(codigoRespuesta, (String) respuesta.get(1));
            }
            return toStr(respuesta.get(4));
        } catch (javax.xml.ws.WebServiceException ex) {
            throw new GELException(CodeError.GEL30060, ex);
        }

    }

   
   

    public void anular() throws GELException {
        try {
            port = crearServicio();
            int fechaTransaccion = fechaEnvio;
            fechaEnvio = obtenerFechaDelSistema();
            horaEnvio = obtenerHoraDelSistema();
            port.meSetReverEcomm(institucionCifrado, secuencial, fechaTransaccion, fechaEnvio, horaEnvio, reto, firmaDigital, passwordCifrado);
        } catch (javax.xml.ws.WebServiceException ex) {
            throw new GELException(CodeError.GEL30060, ex);
        }
    }

    public void registrar() {
        try {
            port = crearServicio();
            validacionDigital = port.setRegistrar(institucionCifrado, HelperLINKSER.getLlavePublicaInstitucion(), passwordCifrado);
            System.out.println(validacionDigital);
        } catch (javax.xml.ws.WebServiceException ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    private ServiciosEcommeLNK crearServicio() {
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
            factory.setAddress((String) config.get("ws.linkser.url"));
            port = factory.create(ServiciosEcommeLNK.class);
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

            client.getInInterceptors().add(new GZIPInInterceptor());
            client.getOutInterceptors().add(new GZIPOutInterceptor());
        }
        return port;
    }

    private int obtenerFechaDelSistema() {
        return Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
    }

    private String obtenerHoraDelSistema() {
        return new SimpleDateFormat("HHmmss").format(new Date());
    }

    private String obtenerImporte(BigDecimal monto) {
        BigDecimal total = monto.multiply(new BigDecimal(100));
        BigDecimal valor = setScale(0, total);
        String result = "000000000000" + valor;
        return result.substring(result.length() - 12);
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

    private String getSecuencial() {
        return new SimpleDateFormat("HHmmss").format(new Date());
    }

    private String toStr(Object data) {
        if (data == null) {
            return "";
        }
        if (data instanceof String) {
            return (String) data;
        }
        return data.toString();
    }

}
