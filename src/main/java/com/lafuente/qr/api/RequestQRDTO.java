/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.qr.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

/**
 *
 * @author dcaceres
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestQRDTO implements Serializable {

    private String qrId;
    private String gloss;
    private String sourceBankId;
    private String originName;
    private String voucherId;
    private String transactionDateTime;

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public String getGloss() {
        return gloss;
    }

    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

    public String getSourceBankId() {
        return sourceBankId;
    }

    public void setSourceBankId(String sourceBankId) {
        this.sourceBankId = sourceBankId;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(String transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    @Override
    public String toString() {
        return "QRDTO{" + "qrId=" + qrId + ", gloss=" + gloss + ", sourceBankId=" + sourceBankId + ", originName=" + originName + ", voucherId=" + voucherId + ", transactionDateTime=" + transactionDateTime + '}';
    }
}
