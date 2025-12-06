package co.retaila.athena.modules.auth.strategies.authorize.responsetypes;

import co.retaila.athena.modules.auth.models.CodeAuthorizeRequestValidationResult;
import co.retaila.athena.modules.auth.requests.AuthorizeRequest;
import co.retaila.athena.modules.auth.responses.AuthorizeResponse;

public interface AuthorizationCodeAuthorizeStrategy {

    boolean canApply(AuthorizeRequest request);
    AuthorizeResponse authorize(AuthorizeRequest request, CodeAuthorizeRequestValidationResult validationResult);

}
