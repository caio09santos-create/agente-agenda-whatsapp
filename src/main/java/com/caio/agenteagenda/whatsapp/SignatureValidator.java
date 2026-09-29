package com.caio.agenteagenda.whatsapp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * A Meta assina o corpo da requisição com HMAC-SHA256 usando o App Secret
 * e envia no cabeçalho "X-Hub-Signature-256: sha256=<hex>".
 */
public class SignatureValidator {

    private static final String PREFIX = "sha256=";
    private final String appSecret;

    public SignatureValidator(String appSecret) {
        this.appSecret = appSecret;
    }

    public boolean isValid(byte[] rawBody, String signatureHeader) {
        if (appSecret == null || appSecret.isBlank()
                || signatureHeader == null || !signatureHeader.startsWith(PREFIX)) {
            return false;
        }
        try {
            byte[] received = HexFormat.of().parseHex(signatureHeader.substring(PREFIX.length()));
            return MessageDigest.isEqual(hmac(rawBody), received); // comparação em tempo constante
        } catch (IllegalArgumentException e) {
            return false; // hex inválido
        }
    }

    byte[] hmac(byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HmacSHA256 indisponível", e);
        }
    }
}
