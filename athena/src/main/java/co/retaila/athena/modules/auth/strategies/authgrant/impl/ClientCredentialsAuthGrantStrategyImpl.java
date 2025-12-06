package co.retaila.athena.modules.auth.strategies.authgrant.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.error.Error;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.exceptions.ValidationException;
import co.retaila.athena.modules.auth.enums.GrantType;
import co.retaila.athena.modules.auth.requests.LoginRequest;
import co.retaila.athena.modules.auth.responses.LoginResponse;
import co.retaila.athena.modules.auth.services.AuthTokenService;
import co.retaila.athena.modules.auth.strategies.authgrant.AuthGrantStrategy;
import co.retaila.athena.modules.client.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientCredentialsAuthGrantStrategyImpl implements AuthGrantStrategy {

    private final ClientService clientService;
    private final AuthTokenService authTokenService;

    @Override
    public boolean canApply(String grantType) {
        return GrantType.CLIENT_CREDENTIALS.getValue().equals(grantType);
    }

    @Override
    public void validateRequest(LoginRequest request) {
        List<Error> errors = new ArrayList<>();

        if (StringUtils.isBlank(request.getClientId()))
            errors.add(Error.create("clientId", "clientId is required"));
        if (StringUtils.isBlank(request.getClientSecret()))
            errors.add(Error.create("clientSecret", "clientSecret is required"));

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Client client = clientService.authenticateClient(request.getClientId(), request.getClientSecret());
        validateClientCanAccessGrant(client);
        return authTokenService.generateAuthToken(client, request.getExpires());
    }

    private void validateClientCanAccessGrant(Client client) {
        if (client.getClientApplicationType().isPublic()) {
            var errorMessage = String.format("Public clients cannot access %s grant type. ", GrantType.CLIENT_CREDENTIALS.getValue());
            throw new AuthException(ErrorConstants.UNAUTHORIZED_CLIENT, errorMessage);
        }
    }

}
