package co.retaila.athena.modules.auth.strategies.authgrant.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.enums.ClientType;
import co.retaila.athena.common.error.Error;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.exceptions.ValidationException;
import co.retaila.athena.modules.auth.enums.GrantType;
import co.retaila.athena.modules.auth.requests.LoginRequest;
import co.retaila.athena.modules.auth.responses.LoginResponse;
import co.retaila.athena.modules.auth.services.AuthTokenService;
import co.retaila.athena.modules.auth.strategies.authgrant.AuthGrantStrategy;
import co.retaila.athena.modules.client.services.ClientService;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordAuthGrantStrategyImpl implements AuthGrantStrategy {

    private final UserService userService;
    private final ClientService clientService;
    private final AuthTokenService authTokenService;
    private final DomainService domainService;

    @Override
    public boolean canApply(String grantType) {
        return GrantType.PASSWORD.getValue().equals(grantType);
    }

    @Override
    public void validateRequest(LoginRequest request) {
        List<Error> errors = new ArrayList<>();

        if (StringUtils.isBlank(request.getClientId()))
            errors.add(Error.create("clientId", "clientId is required"));
        if (StringUtils.isBlank(request.getUsername()))
            errors.add(Error.create("username", "username is required"));
        if (StringUtils.isBlank(request.getPassword()))
            errors.add(Error.create("password", "password is required"));
        if (StringUtils.isBlank(request.getDomain()))
            errors.add(Error.create("domain", "domain is required"));

        if (!errors.isEmpty())
            throw new ValidationException(ErrorConstants.INVALID_REQUEST, errors);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Client client = clientService.validateAuthClient(request.getClientId());
        validateClientCanAccessGrant(client);
        User user = userService.authenticateUser(request.getUsername(), request.getPassword(), request.getDomain());
        Domain domain = domainService.validateAuthDomain(request.getDomain());

        clientService.validateClientHasAccessToDomain(client, domain);
        userService.validateUserDomain(user, domain);

        return authTokenService.generateAuthToken(client, user, request.getExpires());
    }

    private void validateClientCanAccessGrant(Client client) {
        if (!ClientType.FIRST_PARTY.equals(client.getClientType())) {
            var errorMessage = String.format("Client cannot access %s grant type", GrantType.PASSWORD.getValue());
            throw new AuthException(ErrorConstants.UNAUTHORIZED_CLIENT, errorMessage);
        }
    }

}
