/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dcaceres
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitudCompraDTO implements Serializable {

    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private String telefono;
    private String numeroDocumento;
    private String extensionDocumento;
    private ProyectoDTO proyecto;
    private LoteDTO lote;
    private PlazoDTO plazo;
    private SimulacionDTO venta;
    private String mensaje;

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getExtensionDocumento() {
        return extensionDocumento;
    }

    public void setExtensionDocumento(String extensionDocumento) {
        this.extensionDocumento = extensionDocumento;
    }

    public ProyectoDTO getProyecto() {
        return proyecto;
    }

    public void setProyecto(ProyectoDTO proyecto) {
        this.proyecto = proyecto;
    }

    public LoteDTO getLote() {
        return lote;
    }

    public void setLote(LoteDTO lote) {
        this.lote = lote;
    }

    public PlazoDTO getPlazo() {
        return plazo;
    }

    public void setPlazo(PlazoDTO plazo) {
        this.plazo = plazo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public SimulacionDTO getVenta() {
        return venta;
    }

    public void setVenta(SimulacionDTO venta) {
        this.venta = venta;
    }

    public Map<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("nombres", nombres);
        map.put("primerApellido", primerApellido);
        map.put("segundoApellido", segundoApellido);
        map.put("telefono", telefono);
        map.put("numeroDocumento", numeroDocumento);
        map.put("extensionDocumento", extensionDocumento);

        HashMap<String, Object> elem = new HashMap<>();
        elem.put("descripcion", proyecto.getMaktx());
        elem.put("moneda", proyecto.getWaerk());
        map.put("proyecto", elem);

        elem = new HashMap<>();
        elem.put("proyecto", lote.getProyecto());
        elem.put("uv", lote.getUv());
        elem.put("mz", lote.getMz());
        elem.put("lt", lote.getLt());
        elem.put("lote", lote.getLote());
        map.put("lote", elem);

        elem = new HashMap<>();
        elem.put("lote", lote.getLt());
        elem.put("categoria", lote.getCategoria());
        elem.put("superficie", lote.getSuperficie());
        map.put("contrato", elem);

        map.put("porcentaje", venta.getDescuento());

        if ("Z001".equals(plazo.getTipoPago())) {
            map.put("tipoCompra", "CONTADO");            
            BigDecimal precioReferencial = new BigDecimal(venta.getPrecioLista().doubleValue() - venta.getPrecioDescuento().doubleValue());           
            map.put("precioReferencial", precioReferencial);
        }
        if ("Z002".equals(plazo.getTipoPago())) {
            map.put("tipoCompra", "CREDITO");
            elem = new HashMap<>();
            elem.put("plazo", plazo.getPlazo());
            map.put("selectPlazo", elem);
            map.put("fechaPagoDisplay", obtenerFecha(venta.getCuotas().get(0).getFecha()) );

            elem = new HashMap<>();
            elem.put("descuento", venta.getPrecioDescuento());
            elem.put("precio_lista_ajuste", venta.getPrecioListaAjustado());
            elem.put("nuevo_precio_venta", venta.getPrecioVenta());
            elem.put("nuevo_total_pp", venta.getTotalPlanPago());
            map.put("contratoHeader", elem);

            map.put("cuotaInicial", venta.getPrecioVenta().subtract(venta.getTotalPlanPago()));
            List<Map<String, Object>> lista = new ArrayList<>();
            for (CuotaDTO cuota : venta.getCuotas()) {
                elem = new HashMap<>();
                elem.put("z_nro_cuota", cuota.getNumeroCuota());
                elem.put("z_fecha", obtenerFecha(cuota.getFecha()));
                elem.put("z_valor_cuota", cuota.getValorCuota());
                elem.put("z_total", cuota.getSaldo());
                lista.add(elem);
            }
            map.put("detalleSimulacion", lista);
        }
        return map;
    }

    private String obtenerFecha(String fecha) {
        return fecha.substring(8, 10) + "-" + fecha.substring(5, 7) + "-" + fecha.substring(0, 4);
    }
}
