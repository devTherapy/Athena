package co.retaila.athena.modules.auth.services.impl;

import co.retaila.athena.common.constants.DomainConstants;
import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.error.Error;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.exceptions.InvalidOperationException;
import co.retaila.athena.common.exceptions.ServerErrorException;
import co.retaila.athena.common.exceptions.ValidationException;
import co.retaila.athena.modules.auth.enums.AuthResponseType;
import co.retaila.athena.modules.auth.enums.GrantType;
import co.retaila.athena.modules.auth.models.AuthorizeDeviceRequestValidationResult;
import co.retaila.athena.modules.auth.requests.AuthorizeDeviceRequest;
import co.retaila.athena.modules.auth.requests.AuthorizeRequest;
import co.retaila.athena.modules.auth.requests.DeviceLoginRequest;
import co.retaila.athena.modules.auth.requests.LoginRequest;
import co.retaila.athena.modules.auth.responses.AuthorizeResponse;
import co.retaila.athena.modules.auth.responses.DeviceLoginResponse;
import co.retaila.athena.modules.auth.responses.LoginResponse;
import co.retaila.athena.modules.auth.services.AuthService;
import co.retaila.athena.modules.auth.strategies.authgrant.AuthGrantStrategy;
import co.retaila.athena.modules.auth.strategies.authorize.AuthorizeStrategy;
import co.retaila.athena.modules.client.services.ClientService;
import co.retaila.athena.modules.deviceauthorization.services.DeviceAuthorizationService;
import co.retaila.athena.modules.devicecode.models.DeviceCodeResult;
import co.retaila.athena.modules.devicecode.models.DeviceLoginRequestValidationResult;
import co.retaila.athena.modules.devicecode.services.DeviceCodeService;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.scope.services.ScopeService;
import co.retaila.athena.common.services.LoggedInUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static co.retaila.athena.common.constants.SecurityConstants.MINIMUM_TOKEN_VALIDITY_IN_MINUTES;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final List<AuthGrantStrategy> authGrantStrategies;
    private final List<AuthorizeStrategy> authorizationCodeStrategies;
    private final LoggedInUserService loggedInUserService;
    private final ClientService clientService;
    private final DomainService domainService;
    private final ScopeService scopeService;
    private final DeviceCodeService deviceCodeService;
    private final DeviceAuthorizationService deviceAuthorizationService;

    @Override
    public AuthorizeResponse authorize(AuthorizeRequest request) {
        validateAuthorizeRequest(request);
        var authorizeStrategy = getAuthorizeStrategy(request);
        return authorizeStrategy.authorize(request);
    }

    private void validateAuthorizeRequest(AuthorizeRequest request) {
        validateFields(request);
        validateAuthResponseType(request.getResponseType());
    }

    private void validateFields(AuthorizeRequest request) {
        List<Error> errors = new ArrayList<>();

        if (StringUtils.isBlank(request.getResponseType()))
            errors.add(Error.create("responseType", "responseType is required"));
        if (StringUtils.isBlank(request.getClientId()))
            errors.add(Error.create("clientId", "clientId is required"));
        if (StringUtils.isBlank(request.getDomain()))
            errors.add(Error.create("domain", "domain is required"));
        if (StringUtils.isBlank(request.getRedirectUri()))
            errors.add(Error.create("redirectUri", "redirectUri is required"));
        if (StringUtils.isBlank(request.getScope()))
            errors.add(Error.create("scope", "scope is required"));
        if (StringUtils.isBlank(request.getState()))
            errors.add(Error.create("state", "state is required"));

        if (!errors.isEmpty())
            throw new ValidationException(ErrorConstants.INVALID_REQUEST, errors);
    }

    private void validateAuthResponseType(String authResponseType) {
        if (!AuthResponseType.getAuthResponseTypes().contains(authResponseType))
            throwInvalidResponseTypeAuthException();
    }

    private AuthorizeStrategy getAuthorizeStrategy(AuthorizeRequest request) {
        List<AuthorizeStrategy> strategies = authorizationCodeStrategies.stream().filter(x -> x.canApply(request))
                .collect(Collectors.toList());

        if (strategies.size() > 1) {
            log.error("Invalid configuration of authorize strategies");
            throw new ServerErrorException();
        }

        if (strategies.size() < 1)
            throwInvalidResponseTypeAuthException();

        return strategies.get(0);
    }

    private void throwInvalidResponseTypeAuthException() {
        String validAuthResponseTypes = String.join(", ", AuthResponseType.getAuthResponseTypes());
        String errorMessage = String.format("Invalid responseType. Supported auth response types are: %s", validAuthResponseTypes);
        throw new AuthException(ErrorConstants.INVALID_REQUEST, errorMessage);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        validateLoginRequest(request);
        var authGrantStrategy = getAuthGrantStrategy(request);
        authGrantStrategy.validateRequest(request);
        return authGrantStrategy.login(request);
    }

    private void validateLoginRequest(LoginRequest request) {
        validateFields(request);
        validateGrantType(request.getGrantType());
    }

    private void validateFields(LoginRequest request) {
        List<Error> errors = new ArrayList<>();

        if (StringUtils.isBlank(request.getGrantType()))
            errors.add(Error.create("grantType", "grantType is required"));
        if (request.getExpires() != null && request.getExpires() < MINIMUM_TOKEN_VALIDITY_IN_MINUTES)
            errors.add(Error.create("expires", "must be greater than or equal to 60"));

        if (!errors.isEmpty())
            throw new ValidationException(ErrorConstants.INVALID_REQUEST, errors);
    }

    private void validateGrantType(String grantType) {
        if (!GrantType.getGrantTypes().contains(grantType)) {
            String validGrantTypes = String.join(", ", GrantType.getGrantTypes());
            String errorDescription = String.format("Unsupported grantType received. Supported grant types are: %s", validGrantTypes);
            throw new AuthException(ErrorConstants.UNSUPPORTED_GRANT_TYPE, errorDescription);
        }
    }

    private AuthGrantStrategy getAuthGrantStrategy(LoginRequest request) {
        List<AuthGrantStrategy> strategies = authGrantStrategies.stream()
                .filter(x -> x.canApply(request.getGrantType()))
                .collect(Collectors.toList());

        if (strategies.size() != 1) {
            log.error("Invalid configuration of auth grant strategies");
            throw new ServerErrorException();
        }

        return strategies.get(0);
    }

    @Override
    public DeviceLoginResponse initiateDeviceLogin(DeviceLoginRequest request) {
        DeviceLoginRequestValidationResult validationResult = validateDeviceLoginRequest(request);
        DeviceCodeResult deviceCodeResult = deviceCodeService.generateCodes(validationResult.getClient());
        DeviceCode deviceCode = deviceCodeService.persistDeviceCode(
                deviceCodeResult, request, validationResult
        );

        return buildDeviceLoginResponse(deviceCodeResult, deviceCode);
    }

    private DeviceLoginRequestValidationResult validateDeviceLoginRequest(DeviceLoginRequest request) {
        validateFields(request);

        Client client = clientService.validateAuthClient(request.getClientId());
        Domain domain = validateDomain(request);
        clientService.validateClientHasAccessToDomain(client, domain);

        validateScope(request.getScope(), domain);

        return DeviceLoginRequestValidationResult.builder()
                .client(client)
                .domain(domain)
                .build();
    }

    private void validateFields(DeviceLoginRequest request) {
        List<Error> errors = new ArrayList<>();

        if (StringUtils.isBlank(request.getClientId()))
            errors.add(Error.create("clientId", "clientId is required"));
        if (StringUtils.isBlank(request.getDomain()))
            errors.add(Error.create("domain", "domain is required"));

        if (!errors.isEmpty())
            throw new ValidationException(ErrorConstants.INVALID_REQUEST, errors);
    }

    private Domain validateDomain(DeviceLoginRequest request) {
        Domain domain = domainService.validateAuthDomain(request.getDomain());

        if (DomainConstants.ATHENA.equals(domain.getName()))
            throw new AuthException(ErrorConstants.INVALID_GRANT, "Cannot use device_code flow on auth server's domain");

        return domain;
    }

    private void validateScope(String requestedScope, Domain domain) {
        if (StringUtils.isBlank(requestedScope))
            return;

        scopeService.validateRequestedScope(requestedScope, domain);
    }

    private DeviceLoginResponse buildDeviceLoginResponse(DeviceCodeResult deviceCodeResult, DeviceCode deviceCode) {
        long expiresIn = ChronoUnit.SECONDS.between(LocalDateTime.now(), deviceCode.getExpiry());

        return DeviceLoginResponse.builder()
                .deviceCode(deviceCodeResult.getDeviceCode())
                .userCode(deviceCodeResult.getUserCode())
                .verificationUri(deviceCode.getVerificationUri())
                .expiresIn(expiresIn)
                .interval(deviceCode.getIntervalInSeconds())
                .build();
    }

    @Override
    public void authorizeDevice(AuthorizeDeviceRequest request) {
        var validationResult = validateAuthorizeDeviceRequest(request);
        deviceAuthorizationService.createDeviceAuthorization(
                validationResult.getUser(), validationResult.getDeviceCode(), request.getAuthorized()
        );
    }

    private AuthorizeDeviceRequestValidationResult validateAuthorizeDeviceRequest(AuthorizeDeviceRequest request) {
        DeviceCode deviceCode = validateDeviceCode(request);
        User loggedInUser = getLoggedInUser(deviceCode.getRequestedDomain().getName());

        return AuthorizeDeviceRequestValidationResult.builder()
                .user(loggedInUser)
                .deviceCode(deviceCode)
                .build();
    }

    private DeviceCode validateDeviceCode(AuthorizeDeviceRequest request) {
        DeviceCode deviceCode = deviceCodeService.validateDeviceCode(request.getUserCode());
        deviceAuthorizationService.ensureAuthorizationDoesNotExist(deviceCode);

        if (deviceCode.isExpired())
            throw new AuthException(ErrorConstants.EXPIRED_TOKEN);

        return deviceCode;
    }

    private User getLoggedInUser(String domain) {
        try {
            return loggedInUserService.getLoggedInUser(domain);
        }
        catch (InvalidOperationException ex) {
            throw new AuthException(ErrorConstants.INVALID_REQUEST, ex.getMessage());
        }
    }

}
