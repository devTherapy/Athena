package co.retaila.athena.modules.devicecode.services.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.properties.AppProperties;
import co.retaila.athena.common.repositories.DeviceCodeRepository;
import co.retaila.athena.common.utils.CommonUtils;
import co.retaila.athena.modules.auth.requests.DeviceLoginRequest;
import co.retaila.athena.modules.devicecode.models.DeviceCodeResult;
import co.retaila.athena.modules.devicecode.models.DeviceLoginRequestValidationResult;
import co.retaila.athena.modules.devicecode.queries.ValidateUserCodeQuery;
import co.retaila.athena.modules.devicecode.services.DeviceCodeService;
import co.retaila.athena.modules.devicecode.services.DeviceCodeUsageAttemptCountService;
import co.retaila.athena.modules.devicecode.viewmodels.DeviceCodeViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceCodeServiceImpl implements DeviceCodeService {

    private final BCryptPasswordEncoder passwordEncoder;
    @Valid
    private final AppProperties appProperties;
    private final DeviceCodeRepository deviceCodeRepository;
    private final DeviceCodeUsageAttemptCountService deviceCodeUsageAttemptCountService;

    @Override
    public DeviceCodeViewModel validateUserCode(ValidateUserCodeQuery query) {
        DeviceCode deviceCode = validateDeviceCode(query.getUserCode());
        return DeviceCodeViewModel.from(deviceCode);
    }

    @Override
    public DeviceCode validateDeviceCode(String userCode) {
        List<DeviceCode> unauthorizedDeviceCodes = deviceCodeRepository.fetchUnusedDeviceCodes();

        return unauthorizedDeviceCodes.stream()
                .filter(deviceCode -> passwordEncoder.matches(userCode, deviceCode.getUserCode()))
                .findFirst()
                .orElseThrow(
                        () -> new AuthException(ErrorConstants.INVALID_GRANT, "Invalid user code")
                );
    }

    @Override
    public DeviceCode validateDeviceCode(Client client, String deviceCode) {
        List<DeviceCode> clientDeviceCodes = deviceCodeRepository.findByClient(client);

        return clientDeviceCodes.stream()
                .filter(clientDeviceCode -> passwordEncoder.matches(deviceCode, clientDeviceCode.getDeviceCode()))
                .findFirst()
                .orElseThrow(
                        () -> new AuthException(ErrorConstants.INVALID_GRANT, "Invalid device code")
                );
    }

    @Override
    public DeviceCodeResult generateCodes(Client client) {
        return DeviceCodeResult.builder()
                .deviceCode(generateDeviceCode(client))
                .userCode(generateUserCode(client))
                .build();
    }

    private String generateDeviceCode(Client client) {
        String deviceCode;

        do {
            deviceCode = CommonUtils.generateRandomAlphaNumeric(appProperties.getDeviceCodeLength());
        }
        while (deviceCodeExistsForClient(client, deviceCode));

        return deviceCode;
    }

    private boolean deviceCodeExistsForClient(Client client, String deviceCode) {
        List<DeviceCode> clientDeviceCodes = deviceCodeRepository.findByClient(client);

        return clientDeviceCodes.stream()
                .anyMatch(clientDeviceCode -> passwordEncoder.matches(deviceCode, clientDeviceCode.getDeviceCode()));
    }

    private String generateUserCode(Client client) {
        String userCode;

        do {
            userCode = CommonUtils.generateRandomString(
                    appProperties.getUserCodeLength(), appProperties.getUserCodeAlphabet()
            );
        }
        while (userCodeExistsForClient(client, userCode));

        return userCode;
    }

    private boolean userCodeExistsForClient(Client client, String userCode) {
        List<DeviceCode> clientDeviceCodes = deviceCodeRepository.findByClient(client);

        return clientDeviceCodes.stream()
                .anyMatch(clientDeviceCode -> passwordEncoder.matches(userCode, clientDeviceCode.getUserCode()));
    }

    @Transactional
    @Override
    public DeviceCode persistDeviceCode(DeviceCodeResult deviceCodeResult, DeviceLoginRequest request, DeviceLoginRequestValidationResult validationResult) {
        DeviceCode deviceCode = saveDeviceCode(deviceCodeResult, request, validationResult);
        deviceCodeUsageAttemptCountService.initializeDeviceCodeUsageAttempt(deviceCode);
        return deviceCode;
    }

    private DeviceCode saveDeviceCode(DeviceCodeResult deviceCodeResult, DeviceLoginRequest request, DeviceLoginRequestValidationResult validationResult) {
        int validityInSeconds = validationResult.getDomain().getDeviceCodeValidityInSeconds();

        DeviceCode deviceCode = DeviceCode.builder()
                .client(validationResult.getClient())
                .requestedDomain(validationResult.getDomain())
                .deviceCode(passwordEncoder.encode(deviceCodeResult.getDeviceCode()))
                .userCode(passwordEncoder.encode(deviceCodeResult.getUserCode()))
                .verificationUri(appProperties.getDeviceLoginVerificationUri())
                .requestedScope(request.getScope())
                .expiry(LocalDateTime.now().plusSeconds(validityInSeconds))
                .intervalInSeconds(appProperties.getDeviceLoginPollIntervalInSeconds())
                .isUsed(false)
                .build();

        return deviceCodeRepository.save(deviceCode);
    }

    @Override
    public void markDeviceCodeAsUsed(DeviceCode deviceCode) {
        deviceCodeRepository.markDeviceCodeAsUsed(deviceCode.getId());
    }

}
