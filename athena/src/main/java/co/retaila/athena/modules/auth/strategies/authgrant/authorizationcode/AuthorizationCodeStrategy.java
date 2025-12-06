package co.retaila.athena.modules.auth.strategies.authgrant.authorizationcode;

import co.retaila.athena.modules.auth.requests.LoginRequest;
import co.retaila.athena.modules.auth.responses.LoginResponse;

public interface AuthorizationCodeStrategy {

    boolean canApply(LoginRequest request);
    void validateRequest(LoginRequest request);
    LoginResponse login(LoginRequest request);

}
