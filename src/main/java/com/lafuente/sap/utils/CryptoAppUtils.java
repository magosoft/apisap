/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.sap.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author dcaceres
 */
public class CryptoAppUtils {

    public static String encriptar(String texto)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String secretKey = "seáÁéÉíÍóÓúÚüÜñÑ123456789098748912300007789!#%$&gurIDadAPI-SEGURIDADáÁéÉíÍóÓúÚüÜñÑ123456789098748912300007789!#%$&-gRUpo_La_FUente__áÁéÉíÍóÓúÚüÜñÑ123456789098748912300007789!#%$&(SEcuriTY)=%!_"; // llave
        String base64EncryptedString = "";

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digestOfPassword = md.digest(secretKey.getBytes("UTF-8"));
        byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] plainTextBytes = texto.getBytes("utf-8");
        byte[] buf = cipher.doFinal(plainTextBytes);
        byte[] base64Bytes = Base64.encodeBase64(buf);
        base64EncryptedString = new String(base64Bytes);

        return base64EncryptedString;
    }

    public static String desencriptar(String textoEncriptado) 
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        String secretKey = "seáÁéÉíÍóÓúÚüÜñÑ123456789098748912300007789!#%$&gurIDadAPI-SEGURIDADáÁéÉíÍóÓúÚüÜñÑ123456789098748912300007789!#%$&-gRUpo_La_FUente__áÁéÉíÍóÓúÚüÜñÑ123456789098748912300007789!#%$&(SEcuriTY)=%!_"; // llave
        String base64EncryptedString = "";

        byte[] message = Base64.decodeBase64(textoEncriptado.getBytes("UTF-8"));
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
        byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        SecretKey key = new SecretKeySpec(keyBytes, "DESede");

        Cipher decipher = Cipher.getInstance("DESede");
        decipher.init(Cipher.DECRYPT_MODE, key);

        byte[] plainText = decipher.doFinal(message);
        base64EncryptedString = new String(plainText, "UTF-8");

        return base64EncryptedString;
    }

}
