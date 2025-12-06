package co.retaila.athena.modules.devicecode.services;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.modules.auth.requests.DeviceLoginRequest;
import co.retaila.athena.modules.devicecode.models.DeviceCodeResult;
import co.retaila.athena.modules.devicecode.models.DeviceLoginRequestValidationResult;
import co.retaila.athena.modules.devicecode.queries.ValidateUserCodeQuery;
import co.retaila.athena.modules.devicecode.viewmodels.DeviceCodeViewModel;

public interface DeviceCodeService {

    DeviceCodeViewModel validateUserCode(ValidateUserCodeQuery query);
    DeviceCode validateDeviceCode(String userCode);
    DeviceCode validateDeviceCode(Client client, String deviceCode);
    DeviceCodeResult generateCodes(Client client);
    DeviceCode persistDeviceCode(
            DeviceCodeResult deviceCodeResult, DeviceLoginRequest request, DeviceLoginRequestValidationResult validationResult
    );
    void markDeviceCodeAsUsed(DeviceCode deviceCode);

}
