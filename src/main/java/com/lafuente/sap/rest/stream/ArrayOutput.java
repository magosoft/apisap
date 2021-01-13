/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.rest.stream;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

/**
 *
 * @author dcaceres
 */
public class ArrayOutput implements StreamingOutput {

    private final byte[] data;
    public ArrayOutput(byte[] data) {
        this.data = data;
    }

    @Override
    public void write(OutputStream out) throws IOException, WebApplicationException {
        out.write(data);
    }

}
