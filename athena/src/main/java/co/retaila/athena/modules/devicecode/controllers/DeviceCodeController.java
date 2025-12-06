package co.retaila.athena.modules.devicecode.controllers;

import co.retaila.athena.modules.devicecode.queries.ValidateUserCodeQuery;
import co.retaila.athena.modules.devicecode.services.DeviceCodeService;
import co.retaila.athena.modules.devicecode.viewmodels.DeviceCodeViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_PREFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_SUFFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_VIEW_DEVICE_CODE;

@RestController
@RequestMapping("api/device-codes")
@RequiredArgsConstructor
public class DeviceCodeController {

    private final DeviceCodeService deviceCodeService;

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_DEVICE_CODE + AUTH_SUFFIX)
    @RequestMapping(path = "/validate-user-code", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DeviceCodeViewModel validateUserCode(@Valid ValidateUserCodeQuery query) {
        return deviceCodeService.validateUserCode(query);
    }

}
