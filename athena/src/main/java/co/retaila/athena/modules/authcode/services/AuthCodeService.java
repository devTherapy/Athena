package co.retaila.athena.modules.authcode.services;

import co.retaila.athena.common.entities.AuthCode;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.modules.auth.models.CodeAuthorizeRequestValidationResult;
import co.retaila.athena.modules.auth.requests.AuthorizeRequest;

public interface AuthCodeService {

    AuthCode validateClientAuthCode(Client client, String authCode);
    String generateAuthCode(Client client);
    void persistAuthCode(String authCode, AuthorizeRequest request, CodeAuthorizeRequestValidationResult validationResult);
    void useAuthCode(AuthCode authCode);

}
