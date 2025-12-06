package co.retaila.athena.modules.devicecode.services.impl;

import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.entities.DeviceCodeUsageAttemptCount;
import co.retaila.athena.common.exceptions.ServerErrorException;
import co.retaila.athena.common.repositories.DeviceCodeUsageAttemptCountRepository;
import co.retaila.athena.modules.devicecode.services.DeviceCodeUsageAttemptCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceCodeUsageAttemptCountServiceImpl implements DeviceCodeUsageAttemptCountService {
    
    private final DeviceCodeUsageAttemptCountRepository deviceCodeUsageAttemptCountRepository;

    @Override
    public void initializeDeviceCodeUsageAttempt(DeviceCode deviceCode) {
        DeviceCodeUsageAttemptCount attemptCount = DeviceCodeUsageAttemptCount.builder()
                .deviceCode(deviceCode)
                .attemptCount(0)
                .build();

        deviceCodeUsageAttemptCountRepository.save(attemptCount);
    }

    @Override
    public void incrementDeviceCodeUsageAttemptCount(DeviceCode deviceCode) {
        deviceCodeUsageAttemptCountRepository.incrementAttemptCount(deviceCode.getId());
    }

    @Override
    public DeviceCodeUsageAttemptCount findByDeviceCode(DeviceCode deviceCode) {
        DeviceCodeUsageAttemptCount attemptCount = deviceCodeUsageAttemptCountRepository.findByDeviceCode(deviceCode)
                .orElse(null);

        if (attemptCount == null) {
            log.error(String.format("Usage attempt count not initialized for device code with id: %s", deviceCode.getId()));
            throw new ServerErrorException();
        }

        return attemptCount;
    }

}
