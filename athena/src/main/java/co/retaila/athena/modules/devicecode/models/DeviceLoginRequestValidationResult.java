package co.retaila.athena.modules.devicecode.models;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Domain;
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
public class DeviceLoginRequestValidationResult {

    private Client client;
    private Domain domain;

}
