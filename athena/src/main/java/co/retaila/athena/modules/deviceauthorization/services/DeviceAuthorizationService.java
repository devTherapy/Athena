package co.retaila.athena.modules.deviceauthorization.services;

import co.retaila.athena.common.entities.DeviceAuthorization;
import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.entities.User;

public interface DeviceAuthorizationService {

    void ensureAuthorizationDoesNotExist(DeviceCode deviceCode);
    void createDeviceAuthorization(User loggedInUser, DeviceCode deviceCode, boolean authorized);
    DeviceAuthorization validateDeviceCodeAuthorization(DeviceCode deviceCode);
    void useDeviceAuthorization(DeviceAuthorization authorization);

}
