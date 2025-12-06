package co.retaila.athena.modules.auth.strategies.authgrant.authorizationcode.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.AuthCode;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.error.Error;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.exceptions.ValidationException;
import co.retaila.athena.modules.auth.enums.CodeChallengeMethod;
import co.retaila.athena.modules.auth.requests.LoginRequest;
import co.retaila.athena.modules.auth.responses.LoginResponse;
import co.retaila.athena.modules.auth.services.AuthTokenService;
import co.retaila.athena.modules.auth.strategies.authgrant.authorizationcode.AuthorizationCodeStrategy;
import co.retaila.athena.modules.authcode.services.AuthCodeService;
import co.retaila.athena.modules.authcode.services.AuthCodeUsageAttemptCountService;
import co.retaila.athena.modules.client.services.ClientService;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.user.services.UserService;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PkceAuthorizationCodeStrategyImpl implements AuthorizationCodeStrategy {

    private final AuthCodeService authCodeService;
    private final ClientService clientService;
    private final DomainService domainService;
    private final UserService userService;
    private final AuthTokenService authTokenService;
    private final AuthCodeUsageAttemptCountService authCodeUsageAttemptCountService;

    @Override
    public boolean canApply(LoginRequest request) {
        return StringUtils.isNotBlank(request.getCodeVerifier()) && StringUtils.isBlank(request.getClientSecret());
    }

    @Override
    public void validateRequest(LoginRequest request) {
        List<Error> errors = new ArrayList<>();

        if (StringUtils.isBlank(request.getClientId()))
            errors.add(Error.create("clientId", "clientId is required"));
        if (StringUtils.isBlank(request.getCode()))
            errors.add(Error.create("code", "code is required"));
        if (StringUtils.isBlank(request.getCodeVerifier()))
            errors.add(Error.create("codeVerifier", "codeVerifier is required"));
        if (StringUtils.isBlank(request.getRedirectUri()))
            errors.add(Error.create("redirectUri", "redirectUri is required"));
        if (StringUtils.isBlank(request.getDomain()))
            errors.add(Error.create("domain", "domain is required"));

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Client client = clientService.validateAuthClient(request.getClientId());
        AuthCode authCode = validateClientAuthCode(client, request.getCode());
        authCodeUsageAttemptCountService.incrementAuthCodeUsageAttemptCount(authCode);
        Domain domain = domainService.validateAuthDomain(request.getDomain(), authCode.getRequestedDomain());

        clientService.validateClientHasAccessToDomain(client, domain);
        userService.validateUserDomain(authCode.getUser(), domain);
        validateCodeVerifier(request.getCodeVerifier(), authCode);
        validateRedirectUri(request.getRedirectUri(), authCode);

        var response = authTokenService.generateAuthToken(client, authCode, request.getExpires());
        authCodeService.useAuthCode(authCode);

        return response;
    }

    private AuthCode validateClientAuthCode(Client client, String code) {
        AuthCode authCode = authCodeService.validateClientAuthCode(client, code);

        if (!authCode.isPkceAuthCode())
            throw new AuthException(ErrorConstants.INVALID_GRANT, "Cannot use this authorization code with PKCE parameters");

        return authCode;
    }

    private void validateCodeVerifier(String codeVerifier, AuthCode authCode) {
        if (CodeChallengeMethod.S256.equals(authCode.getCodeChallengeMethod()))
            codeVerifier = getSha256Hash(codeVerifier);

        if (!codeVerifier.equals(authCode.getCodeChallenge()))
            throw new AuthException(ErrorConstants.INVALID_GRANT, "Invalid code verifier");
    }

    private String getSha256Hash(String plaintext) {
        return Hashing.sha256()
                .hashString(plaintext, StandardCharsets.UTF_8)
                .toString();
    }

    private void validateRedirectUri(String redirectUri, AuthCode authCode) {
        if (!redirectUri.equals(authCode.getRequestRedirectUri()))
            throw new AuthException(ErrorConstants.INVALID_REDIRECT_URI);
    }

}
