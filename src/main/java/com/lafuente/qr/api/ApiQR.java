package com.lafuente.qr.api;

import com.lafuente.sap.exceptions.CodeError;
import com.lafuente.sap.exceptions.GELException;
import com.lafuente.sap.rest.dto.PagoDTO;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Guido
 */
public class ApiQR {

    private Client client;
    private String accountId = "6Hymm2Ik5es7j92rM89W4A==";//"6cO0N5dtu10pig+MDxmbug==";//"s9CG8FE7Id75ef2jeX9bUA==";//"I8Bl1/IZBWyZk+qJCaMahw==";//
    private String authorizationId = "q4rN8KZVn+LhPLiGh+ZlJw==";//"713K7PvTlACs1gdmv9jGgA==";//"xGTy/5MpdpjgSeuBPIEVwA==";//
    private String token = "eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9\n"
            + "yZSNobWFjLXNoYTI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy\n"
            + "54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1l\n"
            + "IjoiQk9BIiwiUm9sZSI6InVzZXIiLCJjb21wYW55SWQiOiIxIiwiZXhwIjoxNTY3\n"
            + "NjA4MDYwLCJpc3MiOiJibmIuY29tLmJvIiwiYXVkIjoiQk9BIn0.cra8vJsxizupg\n"
            + "i8zeZGi5MNKdmO0lDygtK2bANRH8fA";

    public ApiQR() {

    }

    public ResponseQRDTO generar(PagoDTO dto) throws GELException {
        try {
            client = ClientBuilder.newClient();
            WebTarget target = client.target("http://test.bnb.com.bo")
                    .path("ClientAuthentication.API/api/v1/auth/token");
            HashMap<String, Object> map = new HashMap<>();
            map.put("accountId", accountId);
            map.put("authorizationId", authorizationId);
            ResponseDTO result = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(map, MediaType.APPLICATION_JSON_TYPE), ResponseDTO.class);
            if (!result.isSuccess()) {
                throw new GELException(503, "QR01", result.getMessage());
            }
            token = "Bearer " + result.getMessage();
            client = ClientBuilder.newClient();
            target = client.target("http://test.bnb.com.bo")
                    .path("QRSimple.API/api/v1/main/getQRWithImageAsync");
            map = new HashMap<>();
            map.put("currency", dto.getMoneda());
            map.put("gloss", dto.toOrderId());
            map.put("amount", dto.getMonto());
            map.put("singleUse", true);
            String fecha = getFecha();
            map.put("expirationDate", fecha);
            Future<ResponseQRDTO> r = target.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", token).async()//
                    .post(Entity.entity(map, MediaType.APPLICATION_JSON_TYPE), ResponseQRDTO.class);
            ResponseQRDTO resultQR = r.get();
            if (!resultQR.isSuccess()) {
                throw new GELException(503, "QR02", result.getMessage());
            }
            resultQR.setExpiracion(fecha);
            return resultQR;
        } catch (ExecutionException | InterruptedException ex) {
            throw new GELException(CodeError.GEL30080, ex);
        }
    }

    private String getFecha() {
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentDate.getTime());

    }

    public ResponseEstadoQRDTO getEstado(String id) throws GELException {
        try {
            client = ClientBuilder.newClient();
            WebTarget target = client.target("http://test.bnb.com.bo")
                    .path("ClientAuthentication.API/api/v1/auth/token");
            HashMap<String, Object> map = new HashMap<>();
            map.put("accountId", accountId);
            map.put("authorizationId", authorizationId);
            ResponseDTO result = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(map, MediaType.APPLICATION_JSON_TYPE), ResponseDTO.class);
            if (!result.isSuccess()) {
                throw new GELException(503, "QR01", result.getMessage());
            }
            token = "Bearer " + result.getMessage();
            client = ClientBuilder.newClient();
            target = client.target("http://test.bnb.com.bo")
                    .path("QRSimple.API/api/v1/main/getQRStatusAsync");
            map = new HashMap<>();
            map.put("qrId", id);
            Future<ResponseEstadoQRDTO> r = target.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", token).async()//
                    .post(Entity.entity(map, MediaType.APPLICATION_JSON_TYPE), ResponseEstadoQRDTO.class);
            ResponseEstadoQRDTO resultQR = r.get();
            if (!resultQR.isSuccess()) {
                throw new GELException(503, "QR02", result.getMessage());
            }
            return resultQR;
        } catch (ExecutionException | InterruptedException ex) {
            throw new GELException(CodeError.GEL30080, ex);
        }
    }

}
