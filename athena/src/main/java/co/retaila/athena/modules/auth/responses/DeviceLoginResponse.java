package co.retaila.athena.modules.auth.responses;

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
public class DeviceLoginResponse {

    private String deviceCode;
    private String userCode;
    private String verificationUri;
    private long expiresIn;
    private int interval;

}
