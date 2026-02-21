package com.auth.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

    public AuthResponse(String access, String refresh) {
        this.accessToken=access;
        this.refreshToken=refresh;
    }

}
