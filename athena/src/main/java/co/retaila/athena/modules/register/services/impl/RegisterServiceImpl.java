package co.retaila.athena.modules.register.services.impl;

import co.retaila.athena.common.exceptions.BadRequestException;
import co.retaila.athena.modules.authority.requests.CreateAuthorityRequest;
import co.retaila.athena.modules.authority.services.AuthorityService;
import co.retaila.athena.modules.register.requests.RegistrationRequest;
import co.retaila.athena.modules.register.services.RegisterService;
import co.retaila.athena.modules.resourceserver.requests.CreateResourceServerRequest;
import co.retaila.athena.modules.resourceserver.services.ResourceServerService;
import co.retaila.athena.modules.role.requests.CreateRoleRequest;
import co.retaila.athena.modules.role.requests.UpdateRoleAuthoritiesRequest;
import co.retaila.athena.modules.role.services.RoleService;
import co.retaila.athena.modules.scope.requests.CreateScopeRequest;
import co.retaila.athena.modules.scope.requests.UpdateScopeAuthoritiesRequest;
import co.retaila.athena.modules.scope.services.ScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final AuthorityService authorityService;
    private final ResourceServerService resourceServerService;
    private final RoleService roleService;
    private final ScopeService scopeService;

    @Override
    public void register(RegistrationRequest request) {
        registerResourceServer(request);
        registerAuthorities(request);
        registerRoles(request);
        addAuthoritiesToRoles(request);
        registerScopes(request);
        addAuthoritiesToScopes(request);
    }

    private void registerResourceServer(RegistrationRequest request) {
        try {
            var createResourceServerRequest = CreateResourceServerRequest.from(request);
            resourceServerService.createResourceServer(createResourceServerRequest);
        }
        catch (BadRequestException ignored) {}
    }

    private void registerAuthorities(RegistrationRequest request) {
        for (var authority : request.getAuthorities()) {
            String authorityName = request.getNamespace() + authority.getName();

            if (authorityService.authorityExists(authorityName, request.getDomain()))
                continue;

            createAuthority(request, authority);
        }
    }

    private void createAuthority(RegistrationRequest request, RegistrationRequest.Authority authority) {
        var createAuthorityRequest = CreateAuthorityRequest.from(authority, request);
        authorityService.createAuthority(createAuthorityRequest);
    }

    private void registerRoles(RegistrationRequest request) {
        for (var role : request.getRoles())
            createRole(role, request.getDomain());
    }

    private void createRole(RegistrationRequest.Role role, String domain) {
        try {
            var createRoleRequest = CreateRoleRequest.from(role, domain);
            roleService.createRole(createRoleRequest);
        }
        catch (BadRequestException ignored) {}
    }

    private void addAuthoritiesToRoles(RegistrationRequest request) {
        for (var mapping : request.getRoleAuthoritiesMappings())
            addAuthoritiesToRole(mapping, request.getNamespace(), request.getDomain());
    }

    private void addAuthoritiesToRole(RegistrationRequest.RoleAuthoritiesMapping mapping, String namespace, String domain) {
        try {
            var updateRoleAuthoritiesRequest = UpdateRoleAuthoritiesRequest.from(mapping.getAuthorities(), namespace);
            roleService.addAuthoritiesToRole(mapping.getRole(), domain, updateRoleAuthoritiesRequest);
        }
        catch (BadRequestException ignored) {}
    }

    private void registerScopes(RegistrationRequest request) {
        for (var scope : request.getScopes())
            createScope(scope, request.getDomain());
    }

    private void createScope(RegistrationRequest.Scope scope, String domain) {
        try {
            var createScopeRequest = CreateScopeRequest.from(scope, domain);
            scopeService.createScope(createScopeRequest);
        }
        catch (BadRequestException ignored) {}
    }

    private void addAuthoritiesToScopes(RegistrationRequest request) {
        for (var mapping : request.getScopeAuthoritiesMappings())
            addAuthoritiesToScope(mapping, request.getNamespace(), request.getDomain());
    }

    private void addAuthoritiesToScope(RegistrationRequest.ScopeAuthoritiesMapping mapping, String namespace, String domain) {
        try {
            var updateScopeAuthoritiesRequest = UpdateScopeAuthoritiesRequest.from(mapping.getAuthorities(), namespace);
            scopeService.addAuthoritiesToScope(mapping.getScope(), domain, updateScopeAuthoritiesRequest);
        }
        catch (BadRequestException ignored) {}
    }

}
