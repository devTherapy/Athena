package co.retaila.athena.modules.client.controllers;

import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.client.queries.SearchClientsQuery;
import co.retaila.athena.modules.client.requests.CreateClientRequest;
import co.retaila.athena.modules.client.requests.UpdateClientAuthoritiesRequest;
import co.retaila.athena.modules.client.requests.UpdateClientDomainsRequest;
import co.retaila.athena.modules.client.requests.UpdateClientRequest;
import co.retaila.athena.modules.client.requests.UpdateClientResourceServersRequest;
import co.retaila.athena.modules.client.services.ClientService;
import co.retaila.athena.modules.client.viewmodels.ClientDetails;
import co.retaila.athena.modules.client.viewmodels.ClientViewModel;
import co.retaila.athena.modules.domain.viewmodels.DomainViewModel;
import co.retaila.athena.modules.resourceserver.viewmodels.ResourceServerViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.List;

import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_PREFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_SUFFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_CREATE_CLIENT;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_SEARCH_CLIENTS;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_UPDATE_CLIENT;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_VIEW_CLIENT;

@RestController
@RequestMapping("api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PreAuthorize(AUTH_PREFIX + CAN_SEARCH_CLIENTS + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Page<ClientViewModel> searchClients(@Valid SearchClientsQuery query) {
        return clientService.searchClients(query);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ClientViewModel getClient(@PathVariable long id) {
        return clientService.getClient(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/identifier/{identifier}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ClientViewModel getClient(@PathVariable String identifier) {
        return clientService.getClient(identifier);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_CREATE_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDetails createClient(@Valid @RequestBody CreateClientRequest request) {
        return clientService.createClient(request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public ClientViewModel updateClient(@PathVariable @Min(1) long id, @Valid @RequestBody UpdateClientRequest request) {
        return clientService.updateClient(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/domains", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<DomainViewModel> getClientDomains(@PathVariable @Min(1) Long id) {
        return clientService.getClientDomains(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/add-domains", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addDomainsToClient(@PathVariable @Min(1) long id, @Valid @RequestBody UpdateClientDomainsRequest request) {
        clientService.addDomainsToClient(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/remove-domains", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeDomainsFromClient(@PathVariable @Min(1) long id, @Valid @RequestBody UpdateClientDomainsRequest request) {
        clientService.removeDomainsFromClient(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/resource-servers", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<ResourceServerViewModel> getClientResourceServers(@PathVariable @Min(1) Long id) {
        return clientService.getClientResourceServers(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/add-resource-servers", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addResourceServersToClient(@PathVariable @Min(1) long id, @Valid @RequestBody UpdateClientResourceServersRequest request) {
        clientService.addResourceServersToClient(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/remove-resource-servers", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeResourceServersFromClient(@PathVariable long id, @Valid @RequestBody UpdateClientResourceServersRequest request) {
        clientService.removeResourceServersFromClient(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/authorities", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorityViewModel> getClientAuthorities(@PathVariable @Min(1) Long id) {
        return clientService.getClientAuthorities(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/add-authorities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addAuthoritiesToClient(@PathVariable @Min(1) long id, @Valid @RequestBody UpdateClientAuthoritiesRequest request) {
        clientService.addAuthoritiesToClient(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_CLIENT + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/remove-authorities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeAuthoritiesFromClient(@PathVariable long id, @Valid @RequestBody UpdateClientAuthoritiesRequest request) {
        clientService.removeAuthoritiesFromClient(id, request);
    }

}
