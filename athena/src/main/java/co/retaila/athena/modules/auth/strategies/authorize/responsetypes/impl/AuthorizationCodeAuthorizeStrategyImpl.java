package co.retaila.athena.modules.auth.strategies.authorize.responsetypes.impl;

import co.retaila.athena.modules.auth.models.CodeAuthorizeRequestValidationResult;
import co.retaila.athena.modules.auth.requests.AuthorizeRequest;
import co.retaila.athena.modules.auth.responses.AuthorizeResponse;
import co.retaila.athena.modules.auth.strategies.authorize.responsetypes.AuthorizationCodeAuthorizeStrategy;
import co.retaila.athena.modules.authcode.services.AuthCodeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationCodeAuthorizeStrategyImpl implements AuthorizationCodeAuthorizeStrategy {

    private final AuthCodeService authCodeService;

    @Override
    public boolean canApply(AuthorizeRequest request) {
        return StringUtils.isBlank(request.getCodeChallenge()) && request.getCodeChallengeMethod() == null;
    }

    @Override
    public AuthorizeResponse authorize(AuthorizeRequest request, CodeAuthorizeRequestValidationResult validationResult) {
        String authCode = authCodeService.generateAuthCode(validationResult.getClient());
        authCodeService.persistAuthCode(authCode, request, validationResult);

        return AuthorizeResponse.builder()
                .code(authCode)
                .state(request.getState())
                .build();
    }

}
