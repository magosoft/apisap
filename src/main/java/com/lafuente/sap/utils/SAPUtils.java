package com.lafuente.sap.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author guido
 */
public class SAPUtils {

    public static Map<String, String> strToMap(String format) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = format.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 1) {
                map.put(keyValue[0], "");
            } else {
                map.put(keyValue[0], pair.substring(keyValue[0].length()));
            }
        }
        return map;
    }

    public static String camelCase(String cadena) {
        if (cadena == null) {
            return "";
        }
        String result = "";
        boolean upper = false;
        for (int i = 0; i < cadena.length(); i++) {
            if (cadena.charAt(i) == '_') {
                upper = true;
            } else if (upper) {
                result += Character.toUpperCase(cadena.charAt(i));
                upper = false;
            } else {
                result += Character.toLowerCase(cadena.charAt(i));
            }
        }
        return result;
    }

    public static String dateToStr(Date fecha) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(fecha);
    }

    public static String fechaSistema() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static XMLGregorianCalendar strToHora(String hora) {
        try {
            DateFormat format = new SimpleDateFormat("HH:mm:ss");
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(format.parse(hora));
            XMLGregorianCalendar result = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            result.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
            result.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            return result;
        } catch (DatatypeConfigurationException | ParseException ex) {
            return null;
        }
    }

    public static XMLGregorianCalendar horaSistema() {

        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            XMLGregorianCalendar result = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            result.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
            result.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            return result;
        } catch (DatatypeConfigurationException ex) {
            return null;
        }

    }

    public static String literalMoneda(BigDecimal valor, String moneda) {
        int integerPart = valor.toBigInteger().intValue();
        int fractionalPart = (int) (valor.remainder(BigDecimal.ONE).doubleValue() * 100);
        String literal;
        if (integerPart == 0) {
            literal = "CERO ";
        } else if (integerPart > 999999) {//si es millon
            literal = getMillones(integerPart);
        } else if (integerPart > 999) {//si es miles
            literal = getMiles(integerPart);
        } else if (integerPart > 99) {//si es centena
            literal = getCentenas(integerPart);
        } else if (integerPart > 9) {//si es decena
            literal = getDecenas(integerPart);
        } else {
            literal = getUnidades(integerPart);
        }

        if ("USD".equals(moneda)) {
            return literal + fractionalPart + "/100 DÓLARES AMERICANOS";
        }
        return literal + fractionalPart + "/100 BOLIVIANOS";

    }
    private static final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ", "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE "};
    private static final String[] DECENAS = {"DIEZ ", "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS ",
        "DIECISIETE ", "DIECIOCHO ", "DIECINUEVE", "VEINTE ", "TREINTA ", "CUARENTA ",
        "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA "};
    private static final String[] CENTENAS = {"", "CIENTO ", "DOCIENTOS ", "TRECIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ",
        "SIETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};

    private static String getMillones(int value) {
        //se obtiene los miles
        int miles = value % 1000000;
        //se obtiene los millones
        int millon = value / 1000000;
        String result = "";
        if (miles > 999) {
            result = getMiles(miles);
        } else if (miles > 99) {
            result = getCentenas(miles);
        } else if (miles > 9) {
            result = getDecenas(miles);
        } else if (miles > 0) {
            result = getUnidades(miles);
        }

        if (millon > 999) {
            return getMiles(millon) + "MILLONES " + result;
        } else if (millon > 99) {
            return getCentenas(millon) + "MILLONES " + result;
        } else if (millon > 9) {
            return getDecenas(millon) + "MILLONES " + result;
        } else if (millon > 0) {
            return getUnidades(millon) + "MILLON " + result;
        }
        return result;
    }

    private static String getMiles(int value) {
        //obtiene las centenas
        int c = value % 1000;
        int m = value / 1000;
        String result = "";
        if (c > 99) {
            result = getCentenas(c);
        } else if (c > 9) {
            result = getDecenas(c);
        } else if (c > 0) {
            result = getUnidades(c);
        }
        if (m > 99) {
            return getCentenas(m) + "MIL " + result;
        } else if (m > 9) {
            return getDecenas(m) + "MIL " + result;
        } else if (m > 0) {
            result = getUnidades(m) + "MIL " + result;
        }
        return result;
    }

    private static String getCentenas(int value) {
        int d = value % 100;
        int c = value / 100;
        String result = "";
        if (d > 9) {
            result = getDecenas(d);
        } else if (d > 0) {
            result = getUnidades(d);
        }
        if (value == 100) {
            return "CIEN ";
        } else if (c > 0 && c < 10) {
            return CENTENAS[c] + result;
        }
        return result;
    }

    private static String getDecenas(int value) {
        int u = value % 10;
        int d = value / 10;
        if (value > 19) {//para 20...99
            if (u > 0) { //para 20,30,40,50,60,70,80,90
                return DECENAS[d + 8] + "Y " + getUnidades(u);
            } else {
                return DECENAS[d + 8];
            }
        } else {//numeros entre 11 y 19
            return DECENAS[value - 10];
        }
    }

    private static String getUnidades(int value) {
        return UNIDADES[value];
    }

}
