package co.retaila.athena.modules.client.models;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.ResourceServer;
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
public class ClientCreationRequestValidationResult {

    private List<Authority> authorities;
    private List<Domain> domains;
    private List<ResourceServer> resourceServers;

}
