package co.retaila.athena.modules.auth.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String grantType;
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String domain;
    private String code;
    private String deviceCode;
    private String codeVerifier;
    private String redirectUri;
    private Integer expires;

}
