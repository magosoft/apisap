package com.lafuente.sap.ws;

import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.rest.dto.ItemDTO;
import com.lafuente.sap.rest.dto.PagoDTO;
import com.lafuente.sap.utils.SAPUtils;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobrConsCuotasEStr;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobrConsCuotasETt;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasEcTt;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasIcStr;
import com.sap.document.sap.soap.functions.mc_style.ZfiWsCobranzasIcTt;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author GUIDO CACERES PINTO
 */
public class HelperSAP {

    public static final String RESERVA = "R";
    public static final String CONTRATO = "C";
    private static final String SUCURSAL = "123";
    private static final String AGENCIA = "456";
    private static final String NUMERO = "6569246";

    public static void validarDTO(PagoDTO dto) throws GELException {
        if (dto == null) {
            throw new GELException(CodeError.GEL30018);
        }
        if (dto.getItems() == null) {
            throw new GELException(CodeError.GEL30017);
        }
        if (dto.getItems().isEmpty()) {
            throw new GELException(CodeError.GEL30017);
        }
        if (StringUtils.isEmpty(dto.getSucursal())) {
            dto.setSucursal(SUCURSAL);
        }
        if (StringUtils.isEmpty(dto.getAgencia())) {
            dto.setAgencia(AGENCIA);
        }
        String matnr = "000000000000000000" + dto.getMatnr();
        dto.setMatnr(matnr.substring(matnr.length() - 18));
        BigDecimal sum = BigDecimal.ZERO;
        //double sum = 0;

        for (ItemDTO r1 : dto.getItems()) {
            String seqcuo = "000" + r1.getSeqcuo();
            r1.setSeqcuo(seqcuo.substring(seqcuo.length() - 3));
            sum = sum.add(r1.getImpcuo());

        }
        if (dto.getMonto().doubleValue() != sum.doubleValue()) {
            throw new GELException("GEL30016", "La sumatoria de las cuotas no es igual: " + dto.getMonto().doubleValue() + " <> " + sum.doubleValue());
        }
    }

    public static Map<String, String> createResponse(ZfiWsCobranzasEcTt r1, String asicon) throws GELException {
        Map<String, String> map = new HashMap<>();
        map.put("doccon", r1.getItem().get(0).getDoccon());
        map.put("asicon", asicon);
        i("[PAGO EXITOSO] CLIENTE: " + r1.getItem().get(0).getNomcli() + ", HORA: " + r1.getItem().get(0).getHora() + ", DATO TERRENO: " + r1.getItem().get(0).getDatter());
        return map;
    }

    /*public static Map<String, String> createResponse(ZfiWsCobranzasEcTt r1) throws GELException {
        Map<String, String> map = new HashMap<>();
        map.put("doccon", r1.getItem().get(0).getDoccon());
        map.put("asicon", r1.getItem().get(0).getAsicon());
        i("[PAGO EXITOSO] CLIENTE: " + r1.getItem().get(0).getNomcli() + ", HORA: " + r1.getItem().get(0).getHora() + ", DATO TERRENO: " + r1.getItem().get(0).getDatter());
        return map;
    }*/

    public static ZfiWsCobranzasIcTt getZfiWsCobranzasIcTt(ZfiWsCobrConsCuotasETt r2, PagoDTO dto, String user, String tipoDoc, int cantHist) throws GELException {
        List<ZfiWsCobrConsCuotasEStr> items0 = r2.getItem();
        List<ItemDTO> items1 = dto.getItems();

        if (items0.size() < items1.size()) {
            throw new GELException();
        }

        ZfiWsCobranzasIcTt lista = new ZfiWsCobranzasIcTt();
        String fecha = SAPUtils.fechaSistema();
        XMLGregorianCalendar hora = SAPUtils.horaSistema();
        for (int i = 0; i < items1.size(); i++) {
            ItemDTO i1 = items1.get(i);
            ZfiWsCobrConsCuotasEStr i0 = items0.get(i);

            ZfiWsCobranzasIcStr item = createItem(i0, fecha, hora, user);
            if (RESERVA.equals(tipoDoc)) {
                validarReserva(i0, i1);
                i1.setSeqcuo(i0.getSeqcuo());//Cambiar seqcuo a la reserva
                item.setImpcuo(i1.getImpcuo());//asignar el monto
            } else {
                validarContrato(i0, i1, cantHist);
            }

            lista.getItem().add(item);
        }
        return lista;
    }

    private static ZfiWsCobranzasIcStr createItem(ZfiWsCobrConsCuotasEStr r2, String fechaCobro, XMLGregorianCalendar horaCobro, String cajero) {
        ZfiWsCobranzasIcStr e0 = new ZfiWsCobranzasIcStr();
        e0.setBanco(r2.getBanco());
        e0.setBukrs(r2.getGlosa3());
        e0.setFeccob(fechaCobro);
        e0.setHora(horaCobro);
        e0.setSucurs(SUCURSAL);
        e0.setAgenci(AGENCIA);
        e0.setDocven(r2.getDocven());
        e0.setTipdoc(r2.getTipdoc());
        e0.setSeqcuo(r2.getSeqcuo());
        String fecha = r2.getFecven();
        e0.setFecven(fecha.substring(0, 4) + "-" + fecha.substring(4, 6) + "-" + fecha.substring(6));
        e0.setImpcuo(r2.getImpcuo());
        e0.setMoncuo(r2.getMoncuo());
        e0.setNumid(r2.getNumid());
        e0.setNomcli(r2.getNomcli());
        //e0.setNumcom(NUMERO);
        e0.setCodcaj(cajero);
        return e0;
    }

    private static void validarContrato(ZfiWsCobrConsCuotasEStr i0, ItemDTO i1, int cantHist) throws GELException {
        int n = Integer.parseInt(i1.getSeqcuo()) - cantHist;
        String seqcuo = "000" + n;
        if (!i0.getSeqcuo().equalsIgnoreCase(seqcuo.substring(seqcuo.length() - 3))) {
            i("VALIDACION: " + i0.getSeqcuo() + " <> " + i1.getSeqcuo());
            throw new GELException(CodeError.GEL30019);
        }
        if (!CONTRATO.equalsIgnoreCase(i0.getTipdoc())) {
            i("VALIDACION: " + CONTRATO + " <> " + i0.getTipdoc());
            throw new GELException(CodeError.GEL30013);
        }
        double valorCuota = i0.getImpcuo().doubleValue();
        double valorPago = i1.getImpcuo().doubleValue();
        if (valorPago != valorCuota) {
            i("VALIDACION: " + valorPago + " <> " + valorCuota);
            throw new GELException(CodeError.GEL30014);
        }
    }

    private static void validarReserva(ZfiWsCobrConsCuotasEStr i0, ItemDTO i1) throws GELException {
        if (!RESERVA.equalsIgnoreCase(i0.getTipdoc())) {
            i("VALIDACION: " + RESERVA + " <> " + i0.getTipdoc());
            throw new GELException(CodeError.GEL30003);
        }
        double valorCuota = i0.getImpcuo().doubleValue();
        double valorPago = i1.getImpcuo().doubleValue();
        if (valorPago > valorCuota) {
            i("VALIDACION: " + valorPago + " > " + valorCuota);
            throw new GELException(CodeError.GEL30004);
        }
    }

    private static void i(String message) {
        if (!StringUtils.isEmpty(message)) {
            Logger.getLogger("REST").log(Level.INFO, message);
        }
    }

}
