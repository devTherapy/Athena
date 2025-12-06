package co.retaila.athena.modules.client.services;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.client.queries.SearchClientsQuery;
import co.retaila.athena.modules.client.requests.CreateClientRequest;
import co.retaila.athena.modules.client.requests.UpdateClientAuthoritiesRequest;
import co.retaila.athena.modules.client.requests.UpdateClientDomainsRequest;
import co.retaila.athena.modules.client.requests.UpdateClientRequest;
import co.retaila.athena.modules.client.requests.UpdateClientResourceServersRequest;
import co.retaila.athena.modules.client.viewmodels.ClientDetails;
import co.retaila.athena.modules.client.viewmodels.ClientViewModel;
import co.retaila.athena.modules.domain.viewmodels.DomainViewModel;
import co.retaila.athena.modules.resourceserver.viewmodels.ResourceServerViewModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClientService {

    Page<ClientViewModel> searchClients(SearchClientsQuery query);
    ClientViewModel getClient(long id);
    ClientViewModel getClient(String identifier);
    ClientDetails createClient(CreateClientRequest request);
    ClientViewModel updateClient(long id, UpdateClientRequest request);
    List<DomainViewModel> getClientDomains(Long id);
    void addDomainsToClient(long id, UpdateClientDomainsRequest request);
    void removeDomainsFromClient(long id, UpdateClientDomainsRequest request);
    List<ResourceServerViewModel> getClientResourceServers(Long id);
    void addResourceServersToClient(long id, UpdateClientResourceServersRequest request);
    void removeResourceServersFromClient(long id, UpdateClientResourceServersRequest request);
    List<AuthorityViewModel> getClientAuthorities(Long id);
    void addAuthoritiesToClient(long id, UpdateClientAuthoritiesRequest request);
    void removeAuthoritiesFromClient(long id, UpdateClientAuthoritiesRequest request);
    Client validateAuthClient(String clientId);
    Client authenticateClient(String clientId, String clientSecret);
    void validateClientHasAccessToDomain(Client client, Domain domain);

}
