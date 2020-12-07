package com.lafuente.sap.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 *
 * @author GUIDO CACERES PINTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDTO {

    private String seqcuo;
    private BigDecimal impcuo;

    public String getSeqcuo() {
        return seqcuo;
    }

    public void setSeqcuo(String seqcuo) {
        this.seqcuo = seqcuo;
    }

    public BigDecimal getImpcuo() {
        return impcuo;
    }

    public void setImpcuo(BigDecimal impcuo) {
        this.impcuo = impcuo;
    } 

    @Override
    public String toString() {
        return "ItemDTO{" + "seqcuo=" + seqcuo + ", impcuo=" + impcuo + '}';
    }
    
}
