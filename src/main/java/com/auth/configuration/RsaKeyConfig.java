package com.auth.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

@Component
@Getter
public class RsaKeyConfig {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RsaKeyConfig(
            @Value("${jwt.private-key}") String privatePath,
            @Value("${jwt.public-key}") String publicPath) throws Exception {

        this.privateKey = loadPrivateKey(privatePath);
        this.publicKey = loadPublicKey(publicPath);
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {

        String key = Files.readString(Path.of(path))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }


    private PublicKey loadPublicKey(String path) throws Exception {

        String key = Files.readString(Path.of(path))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

}
