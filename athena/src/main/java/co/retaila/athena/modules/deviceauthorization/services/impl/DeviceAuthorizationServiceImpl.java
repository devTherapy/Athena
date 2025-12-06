package co.retaila.athena.modules.deviceauthorization.services.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.DeviceAuthorization;
import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.entities.DeviceCodeUsageAttemptCount;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.repositories.DeviceAuthorizationRepository;
import co.retaila.athena.modules.auth.services.AuthTokenService;
import co.retaila.athena.modules.deviceauthorization.services.DeviceAuthorizationService;
import co.retaila.athena.modules.devicecode.services.DeviceCodeService;
import co.retaila.athena.modules.devicecode.services.DeviceCodeUsageAttemptCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceAuthorizationServiceImpl implements DeviceAuthorizationService {

    private final DeviceAuthorizationRepository deviceAuthorizationRepository;
    private final AuthTokenService authTokenService;
    private final DeviceCodeService deviceCodeService;
    private final DeviceCodeUsageAttemptCountService deviceCodeUsageAttemptCountService;

    @Override
    public void ensureAuthorizationDoesNotExist(DeviceCode deviceCode) {
        DeviceAuthorization authorization = deviceAuthorizationRepository.findByDeviceCode(deviceCode)
                .orElse(null);

        if (authorization != null)
            throw new AuthException(ErrorConstants.INVALID_GRANT, "Device code already authorized");
    }

    @Override
    public void createDeviceAuthorization(User loggedInUser, DeviceCode deviceCode, boolean authorized) {
        DeviceAuthorization deviceAuthorization = DeviceAuthorization.builder()
                .deviceCode(deviceCode)
                .user(loggedInUser)
                .authorized(authorized)
                .build();

        deviceAuthorizationRepository.save(deviceAuthorization);
    }

    @Override
    public DeviceAuthorization validateDeviceCodeAuthorization(DeviceCode deviceCode) {
        DeviceAuthorization authorization = deviceAuthorizationRepository.findByDeviceCode(deviceCode)
                .orElse(null);

        if (authorization == null)
            throw new AuthException(ErrorConstants.AUTHORIZATION_PENDING);

        if (authorization.isAccessDenied()) {
            deviceCodeService.markDeviceCodeAsUsed(authorization.getDeviceCode());
            throw new AuthException(ErrorConstants.ACCESS_DENIED);
        }

        return authorization;
    }

    /**
     * Caller must not know about user's tokens invalidation. Information omitted from thrown exception.
     *
     * @param authorization must not be {@literal null}.
     */
    @Override
    public void useDeviceAuthorization(DeviceAuthorization authorization) {
        DeviceCodeUsageAttemptCount attemptCount = deviceCodeUsageAttemptCountService.findByDeviceCode(authorization.getDeviceCode());

        if (authorization.getDeviceCode().isUsed() || attemptCount.isMultipleAttempts()) {
            log.warn("Multiple device code usage attempts. Invalidating all user's tokens...");
            authTokenService.invalidateUserTokens(authorization.getUser());
            throw new AuthException(ErrorConstants.INVALID_GRANT, "Multiple device code usage attempts");
        }

        if (authorization.getDeviceCode().isExpired())
            throw new AuthException(ErrorConstants.EXPIRED_TOKEN);

        deviceCodeService.markDeviceCodeAsUsed(authorization.getDeviceCode());
    }

}
