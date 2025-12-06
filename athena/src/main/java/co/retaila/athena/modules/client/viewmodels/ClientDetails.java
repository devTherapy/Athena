package co.retaila.athena.modules.client.viewmodels;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.ClientAuthority;
import co.retaila.athena.common.entities.ClientDomain;
import co.retaila.athena.common.entities.ClientResourceId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDetails {

    private String identifier;
    private String name;
    private String adminEmail;
    private Integer tokenValidityInSeconds;
    private List<String> authorities;
    private List<String> domains;
    private List<String> resourceIds;

    public static ClientDetails from(
            Client client, List<ClientAuthority> clientAuthorities, List<ClientDomain> clientDomains, List<ClientResourceId> clientResourceIds
    ) {
        ClientDetails response = new ClientDetails();

        BeanUtils.copyProperties(client, response);

        List<String> authorities = clientAuthorities.stream()
                .map(clientAuthority -> clientAuthority.getAuthority().getName())
                .collect(Collectors.toList());
        List<String> domains = clientDomains.stream()
                .map(clientDomain -> clientDomain.getDomain().getName())
                .collect(Collectors.toList());
        List<String> resourceIds = clientResourceIds.stream()
                .map(clientResourceId -> clientResourceId.getResourceServer().getResourceId())
                .collect(Collectors.toList());

        response.setAuthorities(authorities);
        response.setDomains(domains);
        response.setResourceIds(resourceIds);

        return response;
    }

}
