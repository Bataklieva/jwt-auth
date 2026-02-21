package com.auth.controller;

import com.auth.configuration.RsaKeyConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class KeyController {

    private final RsaKeyConfig rsaKeys;

    @GetMapping("/public-key")
    public String getPublicKey() {
        return Base64.getEncoder()
                .encodeToString(rsaKeys.getPublicKey().getEncoded());
    }
}
