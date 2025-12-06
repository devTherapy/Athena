package co.retaila.athena.modules.auth.strategies.authgrant;

import co.retaila.athena.modules.auth.requests.LoginRequest;
import co.retaila.athena.modules.auth.responses.LoginResponse;

public interface AuthGrantStrategy {

    boolean canApply(String grantType);
    void validateRequest(LoginRequest request);
    LoginResponse login(LoginRequest request);

}
