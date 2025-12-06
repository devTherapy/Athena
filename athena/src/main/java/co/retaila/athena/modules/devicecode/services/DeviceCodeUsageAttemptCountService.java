package co.retaila.athena.modules.devicecode.services;

import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.entities.DeviceCodeUsageAttemptCount;

public interface DeviceCodeUsageAttemptCountService {

    void initializeDeviceCodeUsageAttempt(DeviceCode deviceCode);
    void incrementDeviceCodeUsageAttemptCount(DeviceCode deviceCode);
    DeviceCodeUsageAttemptCount findByDeviceCode(DeviceCode deviceCode);

}
