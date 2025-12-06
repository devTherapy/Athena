package co.retaila.athena.modules.auth.strategies.authgrant.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.exceptions.ServerErrorException;
import co.retaila.athena.modules.auth.enums.GrantType;
import co.retaila.athena.modules.auth.requests.LoginRequest;
import co.retaila.athena.modules.auth.responses.LoginResponse;
import co.retaila.athena.modules.auth.strategies.authgrant.AuthGrantStrategy;
import co.retaila.athena.modules.auth.strategies.authgrant.authorizationcode.AuthorizationCodeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationCodeAuthGrantStrategyImpl implements AuthGrantStrategy {

    private final List<AuthorizationCodeStrategy> authorizationCodeStrategies;

    @Override
    public boolean canApply(String grantType) {
        return GrantType.AUTHORIZATION_CODE.getValue().equals(grantType);
    }

    @Override
    public void validateRequest(LoginRequest request) {
        var authorizationCodeStrategy = getAuthorizationCodeStrategy(request);
        authorizationCodeStrategy.validateRequest(request);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        var authorizationCodeStrategy = getAuthorizationCodeStrategy(request);
        return authorizationCodeStrategy.login(request);
    }

    private AuthorizationCodeStrategy getAuthorizationCodeStrategy(LoginRequest request) {
        List<AuthorizationCodeStrategy> strategies = authorizationCodeStrategies.stream()
                .filter(x -> x.canApply(request))
                .collect(Collectors.toList());

        if (strategies.size() > 1) {
            log.error("Invalid configuration of authorization code strategies");
            throw new ServerErrorException();
        }

        if (strategies.size() < 1)
            throw new AuthException(ErrorConstants.INVALID_REQUEST, "Send clientSecret or codeVerifier but not both");

        return strategies.get(0);
    }

}
