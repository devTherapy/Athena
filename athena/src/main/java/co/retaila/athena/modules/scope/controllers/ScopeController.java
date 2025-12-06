package co.retaila.athena.modules.scope.controllers;

import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.scope.queries.SearchScopesQuery;
import co.retaila.athena.modules.scope.queries.ValidateRequestedScopeQuery;
import co.retaila.athena.modules.scope.requests.CreateScopeRequest;
import co.retaila.athena.modules.scope.requests.UpdateScopeAuthoritiesRequest;
import co.retaila.athena.modules.scope.services.ScopeService;
import co.retaila.athena.modules.scope.viewmodels.ScopeViewModel;
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
import java.util.List;

import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_PREFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_SUFFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_CREATE_SCOPE;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_SEARCH_SCOPES;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_UPDATE_SCOPE;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_VIEW_SCOPE;

@RestController
@RequestMapping("api/scopes")
@RequiredArgsConstructor
public class ScopeController {

    private final ScopeService scopeService;

    @PreAuthorize(AUTH_PREFIX + CAN_SEARCH_SCOPES + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Page<ScopeViewModel> searchScopes(@Valid SearchScopesQuery query) {
        return scopeService.searchScopes(query);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_SCOPE + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ScopeViewModel getScope(@PathVariable long id) {
        return scopeService.getScope(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_SCOPE + AUTH_SUFFIX)
    @RequestMapping(path = "/{name}/{domain}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ScopeViewModel getScope(@PathVariable String name, @PathVariable String domain) {
        return scopeService.getScope(name, domain);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_SCOPE + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/authorities", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorityViewModel> getScopeAuthorities(@PathVariable long id) {
        return scopeService.getScopeAuthorities(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_SCOPE + AUTH_SUFFIX)
    @RequestMapping(path = "/{name}/{domain}/authorities", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorityViewModel> getScopeAuthorities(@PathVariable String name, @PathVariable String domain) {
        return scopeService.getScopeAuthorities(name, domain);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_SCOPE + AUTH_SUFFIX)
    @RequestMapping(path = "/validate-requested-scope", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<ScopeViewModel> validateRequestedScope(@Valid ValidateRequestedScopeQuery query) {
        return scopeService.validateRequestedScope(query);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_CREATE_SCOPE + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ScopeViewModel createScope(@Valid @RequestBody CreateScopeRequest request) {
        return scopeService.createScope(request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_SCOPE + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/add-authorities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addAuthoritiesToScope(@PathVariable long id, @Valid @RequestBody UpdateScopeAuthoritiesRequest request) {
        scopeService.addAuthoritiesToScope(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_SCOPE + AUTH_SUFFIX)
    @RequestMapping(path = "/{name}/{domain}/add-authorities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addAuthoritiesToScope(@PathVariable String name, @PathVariable String domain, @Valid @RequestBody UpdateScopeAuthoritiesRequest request) {
        scopeService.addAuthoritiesToScope(name, domain, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_SCOPE + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/remove-authorities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeAuthoritiesFromScope(@PathVariable long id, @Valid @RequestBody UpdateScopeAuthoritiesRequest request) {
        scopeService.removeAuthoritiesFromScope(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_SCOPE + AUTH_SUFFIX)
    @RequestMapping(path = "/{name}/{domain}/remove-authorities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeAuthoritiesFromScope(@PathVariable String name, @PathVariable String domain, @Valid @RequestBody UpdateScopeAuthoritiesRequest request) {
        scopeService.removeAuthoritiesFromScope(name, domain, request);
    }

}
