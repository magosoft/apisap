/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lafuente.linkser.ws;

import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import seguridad.criptografia_publica.Claves;
import seguridad.criptografia_publica.CriptografiaPublica;
import seguridad.criptografia_publica.Firma;

/**
 *
 * @author guido
 */
public class HelperLINKSER {

    public static String LLAVE_PUBLICA_LINKSER = "/opt/linkser/LLAVE_PUBLICA_LINKSER/publica.rsa";
    public static String PATH_LLAVE_INSTITUCION = "/opt/linkser/LLAVES_INSTITUCION/";

    public static String cifrarPublicaLinkser(String value) {
        try {
            Claves lnk = new Claves("", LLAVE_PUBLICA_LINKSER);
            return new String(CriptografiaPublica.cipherBloques(value, lnk.getClavePublica()));
        } catch (Exception ex) {
            System.out.println("Error por excepcion cifrarPublicaLinkser: " + ex.getMessage());
        }
        return "";
    }

    public static String getLlavePublicaInstitucion() {
        try {
            Claves claves = new Claves(PATH_LLAVE_INSTITUCION + "privada.rsa", PATH_LLAVE_INSTITUCION + "publica.rsa");
            return new String(claves.getClavePublicaBytes());
        } catch (Exception ex) {
            System.out.println("Error por excepcion getLlavePublicaInstitucion: " + ex.getMessage());
        }
        return "";
    }

    public static String validarFirma(String codigoInstitucion, String reto) {
        try {
            String dataDigital = codigoInstitucion + reto;
            Claves claves = new Claves(PATH_LLAVE_INSTITUCION + "privada.rsa", PATH_LLAVE_INSTITUCION + "publica.rsa");
            return new String(Firma.firmaDigital(dataDigital.getBytes(), claves.getClavePrivada()));
        } catch (Exception ex) {
            System.out.println("Error por excepcion validarFirma: " + ex.getMessage());
        }
        return "";
    }

    private static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();
            while (generatedLength < keyLength + ivLength) {
                if (generatedLength > 0) {
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                }
                md.update(password);
                if (salt != null) {
                    md.update(salt, 0, 8);
                }
                md.digest(generatedData, generatedLength, digestLength);
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }
                generatedLength += digestLength;
            }
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0) {
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);
            }
            return result;

        } catch (DigestException e) {
            throw new RuntimeException(e);
        } finally {
            Arrays.fill(generatedData, (byte) 0);
        }
    }

    public static String desencriptar(String datosEncriptados, Map<String, Object> config) {
        try {
            //byte[] secret = Base64.getDecoder().decode((String) config.get("ws.linkser.secrky"));
            String secret = "seáÁéÉíÍóÓúÚüÜñÑ123456789098748912300007789!#%$&gurIDadAPI-SEGURIDADáÁéÉíÍóÓúÚüÜñÑ123456789098748912300007789!#%$&-gRUpo_La_FUente__áÁéÉíÍóÓúÚüÜñÑ123456789098748912300007789!#%$&(SEcuriTY)=%!_";

            byte[] cipherData = Base64.getDecoder().decode(datosEncriptados);
            byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

            byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(aesCBC.doFinal(encrypted));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException ex) {
            System.err.println(ex);
            return null;
        }
    }
}
