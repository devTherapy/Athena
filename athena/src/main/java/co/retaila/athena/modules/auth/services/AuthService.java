package co.retaila.athena.modules.auth.services;

import co.retaila.athena.modules.auth.requests.AuthorizeDeviceRequest;
import co.retaila.athena.modules.auth.requests.AuthorizeRequest;
import co.retaila.athena.modules.auth.requests.DeviceLoginRequest;
import co.retaila.athena.modules.auth.requests.LoginRequest;
import co.retaila.athena.modules.auth.responses.AuthorizeResponse;
import co.retaila.athena.modules.auth.responses.DeviceLoginResponse;
import co.retaila.athena.modules.auth.responses.LoginResponse;

public interface AuthService {

    AuthorizeResponse authorize(AuthorizeRequest request);
    LoginResponse login(LoginRequest request);
    DeviceLoginResponse initiateDeviceLogin(DeviceLoginRequest request);
    void authorizeDevice(AuthorizeDeviceRequest request);

}
