package co.retaila.athena.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {

    public static final String AUTH_TOKEN_TYPE = "Bearer";

    public static final int MINIMUM_TOKEN_VALIDITY_IN_MINUTES = 60;

    public static final String[] EXCLUDE_FROM_AUTH_URLS = new String[] {
            "/**, OPTIONS",
            "/api/auth/token, POST",
            "/api/auth/device, POST",
            "/.well-known/oauth-authorization-server, GET"
    };
    public static final String[] EXCLUDE_FROM_REQUEST_RESPONSE_LOGGER = new String[] {
            "/**, OPTIONS",
            "/api/auth/**, POST",
            "/api/users, POST"
    };

}
