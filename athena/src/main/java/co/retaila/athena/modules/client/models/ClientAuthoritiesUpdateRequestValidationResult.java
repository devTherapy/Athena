package co.retaila.athena.modules.client.models;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientAuthoritiesUpdateRequestValidationResult {

    private Client client;
    private List<Authority> authorities;

}
