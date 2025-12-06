package co.retaila.athena.modules.register.controllers;

import co.retaila.athena.modules.register.requests.RegistrationRequest;
import co.retaila.athena.modules.register.services.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_PREFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_SUFFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_REGISTER_RESOURCE_SERVER;

@RestController
@RequestMapping("api/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PreAuthorize(AUTH_PREFIX + CAN_REGISTER_RESOURCE_SERVER + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegistrationRequest request) {
        registerService.register(request);
    }

}
