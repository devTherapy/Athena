package co.retaila.athena.modules.auth.models;

import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeDeviceRequestValidationResult {

    private User user;
    private DeviceCode deviceCode;

}
