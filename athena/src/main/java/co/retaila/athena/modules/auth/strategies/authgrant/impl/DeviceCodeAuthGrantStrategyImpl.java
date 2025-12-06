package co.retaila.athena.modules.auth.strategies.authgrant.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.DeviceAuthorization;
import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.error.Error;
import co.retaila.athena.common.exceptions.ValidationException;
import co.retaila.athena.modules.auth.enums.GrantType;
import co.retaila.athena.modules.auth.requests.LoginRequest;
import co.retaila.athena.modules.auth.responses.LoginResponse;
import co.retaila.athena.modules.auth.services.AuthTokenService;
import co.retaila.athena.modules.auth.strategies.authgrant.AuthGrantStrategy;
import co.retaila.athena.modules.client.services.ClientService;
import co.retaila.athena.modules.deviceauthorization.services.DeviceAuthorizationService;
import co.retaila.athena.modules.devicecode.services.DeviceCodeService;
import co.retaila.athena.modules.devicecode.services.DeviceCodeUsageAttemptCountService;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceCodeAuthGrantStrategyImpl implements AuthGrantStrategy {

    private final AuthTokenService authTokenService;
    private final ClientService clientService;
    private final DeviceAuthorizationService deviceAuthorizationService;
    private final DeviceCodeService deviceCodeService;
    private final DeviceCodeUsageAttemptCountService deviceCodeUsageAttemptCountService;
    private final DomainService domainService;
    private final UserService userService;

    @Override
    public boolean canApply(String grantType) {
        return GrantType.DEVICE_CODE.getValue().equals(grantType);
    }

    @Override
    public void validateRequest(LoginRequest request) {
        List<Error> errors = new ArrayList<>();

        if (StringUtils.isBlank(request.getClientId()))
            errors.add(Error.create("clientId", "clientId is required"));
        if (StringUtils.isBlank(request.getDeviceCode()))
            errors.add(Error.create("deviceCode", "deviceCode is required"));
        if (StringUtils.isBlank(request.getDomain()))
            errors.add(Error.create("domain", "domain is required"));

        if (!errors.isEmpty())
            throw new ValidationException(ErrorConstants.INVALID_REQUEST, errors);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Client client = clientService.validateAuthClient(request.getClientId());
        DeviceCode deviceCode = deviceCodeService.validateDeviceCode(client, request.getDeviceCode());
        deviceCodeUsageAttemptCountService.incrementDeviceCodeUsageAttemptCount(deviceCode);
        Domain domain = domainService.validateAuthDomain(request.getDomain(), deviceCode.getRequestedDomain());
        DeviceAuthorization authorization = deviceAuthorizationService.validateDeviceCodeAuthorization(deviceCode);

        clientService.validateClientHasAccessToDomain(client, domain);
        userService.validateUserDomain(authorization.getUser(), domain);

        var response = authTokenService.generateAuthToken(client, authorization.getUser(), deviceCode, request.getExpires());
        deviceAuthorizationService.useDeviceAuthorization(authorization);

        return response;
    }

}
