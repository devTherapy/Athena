package co.retaila.athena.modules.auth.requests;

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
public class DeviceLoginRequest {

    private String clientId;
    private String domain;
    private String scope;

}
