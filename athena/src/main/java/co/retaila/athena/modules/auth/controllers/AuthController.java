package co.retaila.athena.modules.auth.controllers;

import co.retaila.athena.modules.auth.requests.AuthorizeDeviceRequest;
import co.retaila.athena.modules.auth.requests.AuthorizeRequest;
import co.retaila.athena.modules.auth.requests.DeviceLoginRequest;
import co.retaila.athena.modules.auth.requests.LoginRequest;
import co.retaila.athena.modules.auth.responses.AuthorizeResponse;
import co.retaila.athena.modules.auth.responses.DeviceLoginResponse;
import co.retaila.athena.modules.auth.responses.LoginResponse;
import co.retaila.athena.modules.auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @RequestMapping(path = "/authorize", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public AuthorizeResponse authorize(@Valid @RequestBody AuthorizeRequest request) {
        return authService.authorize(request);
    }

    @RequestMapping(path = "/token", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @RequestMapping(path = "/device", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public DeviceLoginResponse initiateDeviceLogin(@Valid @RequestBody DeviceLoginRequest request) {
        return authService.initiateDeviceLogin(request);
    }

    @RequestMapping(path = "/device-authorize", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void authorizeDevice(@Valid @RequestBody AuthorizeDeviceRequest request) {
        authService.authorizeDevice(request);
    }

}
