package com.lafuente.tigomoney.ws;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author TIGO
 */
public class CTripleDes {

    private static final String ALGORITMO = "TripleDes/";
    private static final String ALGORITMO_KEY = "TripleDes";
    private static final String MODE = "ECB/";
    private static final String PADDING = "NOPADDING";
    private static final String UNICODE_CHAR = "UTF-8";

    public static byte[] encrypt(String message, String keypsw)
            throws Exception {
        final byte[] digestOfPassword = keypsw.getBytes(UNICODE_CHAR);
        String transformation = ALGORITMO + MODE + PADDING;
        final SecretKey key = new SecretKeySpec(digestOfPassword, ALGORITMO_KEY);
        final Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        final byte[] plainTextBytes = message.getBytes(UNICODE_CHAR);
        String hexplane = bytesToHex(plainTextBytes);
        hexplane = padString(hexplane);
        final byte[] cipherText = cipher.doFinal(hexToBytes(hexplane));
        return cipherText;
    }

    public static String decrypt(byte[] message, String keypsw)
            throws Exception {
        final byte[] digestOfPassword = keypsw.getBytes(UNICODE_CHAR);
        final SecretKey key = new SecretKeySpec(digestOfPassword, ALGORITMO_KEY);
        final Cipher decipher = Cipher.getInstance(ALGORITMO + MODE + PADDING);
        decipher.init(Cipher.DECRYPT_MODE, key);
        final byte[] plainText = decipher.doFinal(message);
        return new String(plainText, UNICODE_CHAR).trim();
    }

    private static String padString(String source) {
        char paddingChar = '0';
        int size = 8;
        int x = source.length() % size;
        int padLength = size - x;
        StringBuilder sb = new StringBuilder(source);
        for (int i = 0; i < padLength; i++) {
            sb.append(paddingChar);
        }
        x = (sb.length() / size) % size;
        if ((x != 2) && (x % 2 != 0)) {
            for (int i = 0; i < size; i++) {
                sb.append(paddingChar);
            }
        }
        return sb.toString();
    }

    public static String bytesToHex(byte[] bytes) {
        int i;
        StringBuilder builder = new StringBuilder();
        for (i = 0; i < bytes.length; i++) {
            builder.append(String.format("%02X", (bytes[i])));
        }
        return builder.toString();
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(
                        str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }

    }
}
