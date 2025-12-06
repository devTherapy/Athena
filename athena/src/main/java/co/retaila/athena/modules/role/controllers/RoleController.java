package co.retaila.athena.modules.role.controllers;

import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.role.queries.SearchRolesQuery;
import co.retaila.athena.modules.role.requests.CreateRoleRequest;
import co.retaila.athena.modules.role.requests.UpdateRoleAuthoritiesRequest;
import co.retaila.athena.modules.role.services.RoleService;
import co.retaila.athena.modules.role.viewmodels.RoleViewModel;
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
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_CREATE_ROLE;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_SEARCH_ROLES;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_UPDATE_ROLE;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_VIEW_ROLE;

@RestController
@RequestMapping("api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize(AUTH_PREFIX + CAN_SEARCH_ROLES + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Page<RoleViewModel> searchRoles(@Valid SearchRolesQuery query) {
        return roleService.searchRoles(query);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_ROLE + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public RoleViewModel getRole(@PathVariable long id) {
        return roleService.getRole(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_ROLE + AUTH_SUFFIX)
    @RequestMapping(path = "/{name}/{domain}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public RoleViewModel getRole(@PathVariable String name, @PathVariable String domain) {
        return roleService.getRole(name, domain);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_ROLE + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/authorities", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorityViewModel> getRoleAuthorities(@PathVariable long id) {
        return roleService.getRoleAuthorities(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_ROLE + AUTH_SUFFIX)
    @RequestMapping(path = "/{name}/{domain}/authorities", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorityViewModel> getRoleAuthorities(@PathVariable String name, @PathVariable String domain) {
        return roleService.getRoleAuthorities(name, domain);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_CREATE_ROLE + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public RoleViewModel createRole(@Valid @RequestBody CreateRoleRequest request) {
        return roleService.createRole(request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_ROLE + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/add-authorities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addAuthoritiesToRole(@PathVariable long id, @Valid @RequestBody UpdateRoleAuthoritiesRequest request) {
        roleService.addAuthoritiesToRole(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_ROLE + AUTH_SUFFIX)
    @RequestMapping(path = "/{name}/{domain}/add-authorities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addAuthoritiesToRole(@PathVariable String name, @PathVariable String domain, @Valid @RequestBody UpdateRoleAuthoritiesRequest request) {
        roleService.addAuthoritiesToRole(name, domain, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_ROLE + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/remove-authorities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeAuthoritiesFromRole(@PathVariable long id, @Valid @RequestBody UpdateRoleAuthoritiesRequest request) {
        roleService.removeAuthoritiesFromRole(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_ROLE + AUTH_SUFFIX)
    @RequestMapping(path = "/{name}/{domain}/remove-authorities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeAuthoritiesFromRole(@PathVariable String name, @PathVariable String domain, @Valid @RequestBody UpdateRoleAuthoritiesRequest request) {
        roleService.removeAuthoritiesFromRole(name, domain, request);
    }

}
