package co.retaila.athena.modules.auth.services;

import co.retaila.athena.common.entities.AuthCode;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.modules.auth.responses.LoginResponse;

public interface AuthTokenService {

    LoginResponse generateAuthToken(Client client, Integer expires);
    LoginResponse generateAuthToken(Client client, User user, Integer expires);
    LoginResponse generateAuthToken(Client client, AuthCode authCode, Integer expires);
    LoginResponse generateAuthToken(Client client, User user, DeviceCode deviceCode, Integer expires);
    void invalidateUserTokens(User user);

}
