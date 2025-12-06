package co.retaila.athena.modules.auth.strategies.authorize;

import co.retaila.athena.modules.auth.requests.AuthorizeRequest;
import co.retaila.athena.modules.auth.responses.AuthorizeResponse;

public interface AuthorizeStrategy {

    boolean canApply(AuthorizeRequest request);
    AuthorizeResponse authorize(AuthorizeRequest request);

}
