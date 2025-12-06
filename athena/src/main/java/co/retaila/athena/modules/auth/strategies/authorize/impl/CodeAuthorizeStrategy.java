package co.retaila.athena.modules.auth.strategies.authorize.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.constants.DomainConstants;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.exceptions.ServerErrorException;
import co.retaila.athena.common.exceptions.InvalidOperationException;
import co.retaila.athena.modules.auth.enums.AuthResponseType;
import co.retaila.athena.modules.auth.models.CodeAuthorizeRequestValidationResult;
import co.retaila.athena.modules.auth.requests.AuthorizeRequest;
import co.retaila.athena.modules.auth.responses.AuthorizeResponse;
import co.retaila.athena.modules.auth.strategies.authorize.AuthorizeStrategy;
import co.retaila.athena.modules.auth.strategies.authorize.responsetypes.AuthorizationCodeAuthorizeStrategy;
import co.retaila.athena.modules.client.services.ClientService;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.scope.services.ScopeService;
import co.retaila.athena.common.services.LoggedInUserService;
import co.retaila.athena.modules.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeAuthorizeStrategy implements AuthorizeStrategy {

    private final LoggedInUserService loggedInUserService;
    private final ClientService clientService;
    private final DomainService domainService;
    private final ScopeService scopeService;
    private final UserService userService;
    private final List<AuthorizationCodeAuthorizeStrategy> codeAuthorizeStrategies;

    @Override
    public boolean canApply(AuthorizeRequest request) {
        return AuthResponseType.CODE.getValue().equals(request.getResponseType());
    }

    @Override
    public AuthorizeResponse authorize(AuthorizeRequest request) {
        CodeAuthorizeRequestValidationResult validationResult = validateAuthorizeRequest(request);
        AuthorizationCodeAuthorizeStrategy authorizeStrategy = getAuthorizeStrategy(request);
        return authorizeStrategy.authorize(request, validationResult);
    }

    private CodeAuthorizeRequestValidationResult validateAuthorizeRequest(AuthorizeRequest request) {
        Domain domain = validateDomain(request);
        Client client = clientService.validateAuthClient(request.getClientId());
        clientService.validateClientHasAccessToDomain(client, domain);

        User loggedInUser = getLoggedInUser(domain.getName());
        userService.validateUserDomain(loggedInUser, domain);

        validateRedirectUri(request.getRedirectUri(), client);
        scopeService.validateRequestedScope(request.getScope(), domain);

        return CodeAuthorizeRequestValidationResult.builder()
                .user(loggedInUser)
                .client(client)
                .domain(domain)
                .build();
    }

    private Domain validateDomain(AuthorizeRequest request) {
        Domain domain = domainService.validateAuthDomain(request.getDomain());

        if (DomainConstants.ATHENA.equals(domain.getName()))
            throw new AuthException(ErrorConstants.INVALID_GRANT, "Cannot use authorization_code flow on auth server's domain");

        return domain;
    }

    private User getLoggedInUser(String domain) {
        try {
            return loggedInUserService.getLoggedInUser(domain);
        }
        catch (InvalidOperationException ex) {
            throw new AuthException(ErrorConstants.INVALID_REQUEST, ex.getMessage());
        }
    }

    private void validateRedirectUri(String redirectUri, Client client) {
        if (!redirectUri.equals(client.getRedirectUri())) {
            throw new AuthException(ErrorConstants.INVALID_REDIRECT_URI);
        }
    }

    private AuthorizationCodeAuthorizeStrategy getAuthorizeStrategy(AuthorizeRequest request) {
        List<AuthorizationCodeAuthorizeStrategy> strategies = codeAuthorizeStrategies.stream().filter(x -> x.canApply(request))
                .collect(Collectors.toList());

        if (strategies.size() > 1) {
            log.error("Invalid configuration of authorization code authorize strategies");
            throw new ServerErrorException();
        }

        if (strategies.size() < 1)
            throw new AuthException(ErrorConstants.INVALID_REQUEST, "Send codeChallenge and codeChallengeMethod together or not at all");

        return strategies.get(0);
    }

}
