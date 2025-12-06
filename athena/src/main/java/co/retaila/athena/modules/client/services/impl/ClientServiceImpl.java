package co.retaila.athena.modules.client.services.impl;

import co.retaila.athena.common.constants.ClientConstants;
import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.ClientAuthority;
import co.retaila.athena.common.entities.ClientDomain;
import co.retaila.athena.common.entities.ClientResourceId;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.ResourceServer;
import co.retaila.athena.common.enums.ClientType;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.exceptions.BadRequestException;
import co.retaila.athena.common.exceptions.NotFoundException;
import co.retaila.athena.common.repositories.ClientAuthorityRepository;
import co.retaila.athena.common.repositories.ClientDomainRepository;
import co.retaila.athena.common.repositories.ClientRepository;
import co.retaila.athena.common.repositories.ClientResourceIdRepository;
import co.retaila.athena.modules.authority.services.AuthorityService;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.client.models.ClientAuthoritiesUpdateRequestValidationResult;
import co.retaila.athena.modules.client.models.ClientCreationRequestValidationResult;
import co.retaila.athena.modules.client.models.ClientDomainsUpdateRequestValidationResult;
import co.retaila.athena.modules.client.models.ClientResourceServersUpdateRequestValidationResult;
import co.retaila.athena.modules.client.queries.SearchClientsQuery;
import co.retaila.athena.modules.client.requests.CreateClientRequest;
import co.retaila.athena.modules.client.requests.UpdateClientAuthoritiesRequest;
import co.retaila.athena.modules.client.requests.UpdateClientDomainsRequest;
import co.retaila.athena.modules.client.requests.UpdateClientRequest;
import co.retaila.athena.modules.client.requests.UpdateClientResourceServersRequest;
import co.retaila.athena.modules.client.services.ClientService;
import co.retaila.athena.modules.client.viewmodels.ClientDetails;
import co.retaila.athena.modules.client.viewmodels.ClientViewModel;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.domain.viewmodels.DomainViewModel;
import co.retaila.athena.modules.resourceserver.services.ResourceServerService;
import co.retaila.athena.modules.resourceserver.viewmodels.ResourceServerViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientDomainRepository clientDomainRepository;
    private final ClientResourceIdRepository clientResourceIdRepository;
    private final AuthorityService authorityService;
    private final DomainService domainService;
    private final ResourceServerService resourceServerService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ClientAuthorityRepository clientAuthorityRepository;

    @Override
    public Page<ClientViewModel> searchClients(SearchClientsQuery query) {
        return clientRepository.findAll(query.getPredicate(), query.getPageable()).map(ClientViewModel::from);
    }

    @Override
    public ClientViewModel getClient(long id) {
        Client client = validateClient(id);
        return ClientViewModel.from(client);
    }

    @Override
    public ClientViewModel getClient(String identifier) {
        Client client = validateClient(identifier);
        return ClientViewModel.from(client);
    }

    @Transactional
    @Override
    public ClientDetails createClient(CreateClientRequest request) {
        var validationResult = validateClientCreationRequest(request);
        Client client = buildClient(request);
        Client createdClient = clientRepository.save(client);
        List<ClientAuthority> clientAuthorities = createClientAuthorityMappings(createdClient, validationResult.getAuthorities());
        List<ClientDomain> clientDomains = createClientDomainMappings(createdClient, validationResult.getDomains());
        List<ClientResourceId> clientResourceIds = createClientResourceIdMappings(createdClient, validationResult.getResourceServers());

        return ClientDetails.from(createdClient, clientAuthorities, clientDomains, clientResourceIds);
    }

    private ClientCreationRequestValidationResult validateClientCreationRequest(CreateClientRequest request) {
        validateClientIdentifier(request.getIdentifier());
        validateClientName(request.getName());

        List<Domain> domains = domainService.validateDomains(request.getDomains());
        List<ResourceServer> resourceServers = resourceServerService.validateResourceIds(request.getResourceIds());

        List<String> domainNames = domains.stream().map(Domain::getName).collect(Collectors.toList());
        List<Authority> authorities = authorityService.validateAuthorities(request.getAuthorities(), domainNames);

        if (ClientType.FIRST_PARTY.equals(request.getClientType())) {
            domains.add(domainService.getDefaultDomain());
            authorities.addAll(authorityService.getFirstPartyClientAuthorities());
        }

        return ClientCreationRequestValidationResult.builder()
                .authorities(authorities)
                .domains(domains)
                .resourceServers(resourceServers)
                .build();
    }

    private void validateClientIdentifier(String identifier) {
        if (clientRepository.existsByIdentifier(identifier)) {
            throw new BadRequestException(String.format("Client with identifier: '%s' already exists", identifier));
        }
    }

    private void validateClientName(String name) {
        if (clientRepository.existsByName(name)) {
            throw new BadRequestException(String.format("Client with name: '%s' already exists", name));
        }
    }

    private Client buildClient(CreateClientRequest request) {
        Integer tokenValidityInSeconds = request.getTokenValidityInSeconds() != null ?
                request.getTokenValidityInSeconds() : ClientConstants.TOKEN_VALIDITY_IN_SECONDS;

        return Client.builder()
                .identifier(request.getIdentifier())
                .secret(passwordEncoder.encode(request.getSecret()))
                .name(request.getName())
                .adminEmail(request.getAdminEmail())
                .redirectUri(request.getRedirectUri())
                .clientType(request.getClientType())
                .clientApplicationType(request.getClientApplicationType())
                .tokenValidityInSeconds(tokenValidityInSeconds)
                .build();
    }

    @Override
    public ClientViewModel updateClient(long id, UpdateClientRequest request) {
        Client client = validateClient(id);

        client.setAdminEmail(request.getAdminEmail());
        client.setRedirectUri(request.getRedirectUri());
        client.setTokenValidityInSeconds(request.getTokenValidityInSeconds());

        return ClientViewModel.from(clientRepository.save(client));
    }

    @Override
    public List<DomainViewModel> getClientDomains(Long id) {
        Client client = validateClient(id);
        List<Domain> clientDomains = domainService.getClientDomains(client);

        return clientDomains.stream()
                .map(DomainViewModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public void addDomainsToClient(long id, UpdateClientDomainsRequest request) {
        ClientDomainsUpdateRequestValidationResult validationResult = validateClientDomainsUpdateRequest(id, request);

        var domains = validationResult.getDomains().stream()
                .filter(domain -> !clientDomainRepository.existsByClientAndDomain(validationResult.getClient(), domain))
                .collect(Collectors.toList());

        createClientDomainMappings(validationResult.getClient(), domains);
    }

    @Override
    public void removeDomainsFromClient(long id, UpdateClientDomainsRequest request) {
        ClientDomainsUpdateRequestValidationResult validationResult = validateClientDomainsUpdateRequest(id, request);

        var domains = validationResult.getDomains().stream()
                .filter(domain -> clientDomainRepository.existsByClientAndDomain(validationResult.getClient(), domain))
                .collect(Collectors.toList());

        deleteClientDomainMappings(validationResult.getClient(), domains);
    }

    @Override
    public List<ResourceServerViewModel> getClientResourceServers(Long id) {
        Client client = validateClient(id);
        List<ResourceServer> clientResourceServers = resourceServerService.getClientResourceServers(client);

        return clientResourceServers.stream()
                .map(ResourceServerViewModel::from)
                .collect(Collectors.toList());
    }

    private ClientDomainsUpdateRequestValidationResult validateClientDomainsUpdateRequest(long clientId, UpdateClientDomainsRequest request) {
        Client client = validateClient(clientId);
        List<Domain> domains = domainService.validateDomains(request.getDomains());

        return ClientDomainsUpdateRequestValidationResult.builder()
                .client(client)
                .domains(domains)
                .build();
    }

    private List<ClientDomain> createClientDomainMappings(Client client, List<Domain> domains) {
        List<ClientDomain> clientDomains = new ArrayList<>(domains.size());

        for (Domain domain : domains) {
            ClientDomain clientDomain = ClientDomain.builder().domain(domain).client(client).build();
            clientDomains.add(clientDomain);
        }

        return clientDomainRepository.saveAll(clientDomains);
    }

    private void deleteClientDomainMappings(Client client, List<Domain> domains) {
        var domainIds = domains.stream().map(Domain::getId).collect(Collectors.toList());
        clientDomainRepository.deleteByClientAndDomainIn(client.getId(), domainIds);
    }

    @Override
    public void addResourceServersToClient(long id, UpdateClientResourceServersRequest request) {
        ClientResourceServersUpdateRequestValidationResult validationResult = validateClientResourceServersUpdateRequest(id, request);

        var resourceServers = validationResult.getResourceServers().stream()
                .filter(resourceServer -> !clientResourceIdRepository.existsByClientAndResourceServer(validationResult.getClient(), resourceServer))
                .collect(Collectors.toList());

        createClientResourceIdMappings(validationResult.getClient(), resourceServers);
    }

    @Override
    public void removeResourceServersFromClient(long id, UpdateClientResourceServersRequest request) {
        ClientResourceServersUpdateRequestValidationResult validationResult = validateClientResourceServersUpdateRequest(id, request);

        var resourceServers = validationResult.getResourceServers().stream()
                .filter(resourceServer -> clientResourceIdRepository.existsByClientAndResourceServer(validationResult.getClient(), resourceServer))
                .collect(Collectors.toList());

        deleteClientResourceIdMappings(validationResult.getClient(), resourceServers);
    }

    private ClientResourceServersUpdateRequestValidationResult validateClientResourceServersUpdateRequest(long clientId, UpdateClientResourceServersRequest request) {
        Client client = validateClient(clientId);
        List<ResourceServer> resourceServers = resourceServerService.validateResourceIds(request.getResourceIds());

        return ClientResourceServersUpdateRequestValidationResult.builder()
                .client(client)
                .resourceServers(resourceServers)
                .build();
    }

    private List<ClientResourceId> createClientResourceIdMappings(Client client, List<ResourceServer> resourceServers) {
        List<ClientResourceId> clientResourceIds = new ArrayList<>(resourceServers.size());

        for (ResourceServer resourceServer : resourceServers) {
            ClientResourceId clientResourceId = ClientResourceId.builder().resourceServer(resourceServer).client(client).build();
            clientResourceIds.add(clientResourceId);
        }

        return clientResourceIdRepository.saveAll(clientResourceIds);
    }

    private void deleteClientResourceIdMappings(Client client, List<ResourceServer> resourceServers) {
        var resourceServerIds = resourceServers.stream().map(ResourceServer::getId).collect(Collectors.toList());
        clientResourceIdRepository.deleteByClientAndResourceServerIn(client.getId(), resourceServerIds);
    }

    @Override
    public List<AuthorityViewModel> getClientAuthorities(Long id) {
        Client client = validateClient(id);
        List<Authority> clientAuthorities = authorityService.getClientAuthorities(client);

        return clientAuthorities.stream()
                .map(AuthorityViewModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public void addAuthoritiesToClient(long id, UpdateClientAuthoritiesRequest request) {
        ClientAuthoritiesUpdateRequestValidationResult validationResult = validateClientAuthoritiesUpdateRequest(id, request);

        var authorities = validationResult.getAuthorities().stream()
                .filter(authority -> !clientAuthorityRepository.existsByClientAndAuthority(validationResult.getClient(), authority))
                .collect(Collectors.toList());

        createClientAuthorityMappings(validationResult.getClient(), authorities);
    }

    @Override
    public void removeAuthoritiesFromClient(long id, UpdateClientAuthoritiesRequest request) {
        ClientAuthoritiesUpdateRequestValidationResult validationResult = validateClientAuthoritiesUpdateRequest(id, request);

        var authorities = validationResult.getAuthorities().stream()
                .filter(authority -> clientAuthorityRepository.existsByClientAndAuthority(validationResult.getClient(), authority))
                .collect(Collectors.toList());

        deleteClientAuthorityMappings(validationResult.getClient(), authorities);
    }

    private ClientAuthoritiesUpdateRequestValidationResult validateClientAuthoritiesUpdateRequest(long id, UpdateClientAuthoritiesRequest request) {
        Client client = validateClient(id);
        List<String> clientDomains = getClientDomains(client);
        List<Authority> authorities = authorityService.validateAuthorities(request.getAuthorities(), clientDomains);

        return ClientAuthoritiesUpdateRequestValidationResult.builder()
                .client(client)
                .authorities(authorities)
                .build();
    }

    private List<String> getClientDomains(Client client) {
        return domainService.getClientDomains(client).stream()
                .map(Domain::getName)
                .collect(Collectors.toList());
    }

    private List<ClientAuthority> createClientAuthorityMappings(Client client, List<Authority> authorities) {
        List<ClientAuthority> clientAuthorities = new ArrayList<>(authorities.size());

        for (var authority : authorities) {
            ClientAuthority clientAuthority = ClientAuthority.builder().authority(authority).client(client).build();
            clientAuthorities.add(clientAuthority);
        }

        return clientAuthorityRepository.saveAll(clientAuthorities);
    }

    private void deleteClientAuthorityMappings(Client client, List<Authority> authorities) {
        var authorityIds = authorities.stream().map(Authority::getId).collect(Collectors.toList());
        clientAuthorityRepository.deleteByClientAndAuthorityIn(client.getId(), authorityIds);
    }

    private Client validateClient(long id) {
        Optional<Client> clientExists = clientRepository.findById(id);

        if (clientExists.isEmpty()) {
            throw new NotFoundException(String.format("Client with id: '%d' does not exist", id));
        }

        return clientExists.get();
    }

    @Override
    public Client validateAuthClient(String clientId) {
        try {
            return validateClient(clientId);
        }
        catch (NotFoundException ex){
            throw new AuthException(ErrorConstants.INVALID_CLIENT, ex.getErrorDescription());
        }
    }

    private Client validateClient(String identifier) {
        Client client = clientRepository.findByIdentifier(identifier).orElse(null);

        if (client == null)
            throw new NotFoundException(String.format("Client with identifier: '%s' does not exist", identifier));

        return client;
    }

    @Override
    public Client authenticateClient(String clientId, String clientSecret) {
        Optional<Client> clientExists = clientRepository.findByIdentifier(clientId);

        if (clientExists.isEmpty() || !passwordEncoder.matches(clientSecret, clientExists.get().getSecret()))
            throw new AuthException(ErrorConstants.INVALID_CLIENT, "Incorrect client credentials");


        return clientExists.get();
    }

    @Override
    public void validateClientHasAccessToDomain(Client client, Domain domain) {
        if (ClientConstants.ATHENA.equals(client.getIdentifier()))
            return;

        List<String> clientDomains = clientDomainRepository.findByClient(client).stream()
                .map(clientDomain -> clientDomain.getDomain().getName())
                .collect(Collectors.toList());

        boolean noMatch = clientDomains.stream().noneMatch(clientDomain -> clientDomain.equals(domain.getName()));

        if (noMatch)
            throw new AuthException(ErrorConstants.UNAUTHORIZED_CLIENT, "Client does not have access to given domain");
    }

}
