package co.retaila.athena.modules.client.models;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Domain;
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
public class ClientDomainsUpdateRequestValidationResult {

    private Client client;
    private List<Domain> domains;

}
