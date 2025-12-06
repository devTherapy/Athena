package co.retaila.athena.modules.authority.controllers;

import co.retaila.athena.modules.authority.queries.SearchAuthoritiesQuery;
import co.retaila.athena.modules.authority.requests.CreateAuthorityRequest;
import co.retaila.athena.modules.authority.services.AuthorityService;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
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

import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_PREFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_SUFFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_CREATE_AUTHORITY;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_SEARCH_AUTHORITIES;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_VIEW_AUTHORITY;

@RestController
@RequestMapping("api/authorities")
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    @PreAuthorize(AUTH_PREFIX + CAN_SEARCH_AUTHORITIES + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Page<AuthorityViewModel> searchAuthorities(@Valid SearchAuthoritiesQuery query) {
        return authorityService.searchAuthorities(query);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_AUTHORITY + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public AuthorityViewModel getAuthority(@PathVariable long id) {
        return authorityService.getAuthority(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_AUTHORITY + AUTH_SUFFIX)
    @RequestMapping(path = "/{name}/{domain}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public AuthorityViewModel getAuthority(@PathVariable String name, @PathVariable String domain) {
        return authorityService.getAuthority(name, domain);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_CREATE_AUTHORITY + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorityViewModel createAuthority(@Valid @RequestBody CreateAuthorityRequest request) {
        return authorityService.createAuthority(request);
    }

}
