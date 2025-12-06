package co.retaila.athena.modules.role.services.impl;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.RoleAuthority;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.entities.UserRole;
import co.retaila.athena.common.exceptions.BadRequestException;
import co.retaila.athena.common.exceptions.NotFoundException;
import co.retaila.athena.common.repositories.RoleAuthorityRepository;
import co.retaila.athena.common.repositories.RoleRepository;
import co.retaila.athena.common.repositories.UserRoleRepository;
import co.retaila.athena.modules.authority.services.AuthorityService;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.role.models.CreateRoleRequestValidationResult;
import co.retaila.athena.modules.role.models.RoleAuthoritiesUpdateRequestValidationResult;
import co.retaila.athena.modules.role.queries.SearchRolesQuery;
import co.retaila.athena.modules.role.requests.CreateRoleRequest;
import co.retaila.athena.modules.role.requests.UpdateRoleAuthoritiesRequest;
import co.retaila.athena.modules.role.services.RoleService;
import co.retaila.athena.modules.role.viewmodels.RoleViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final DomainService domainService;
    private final UserRoleRepository userRoleRepository;
    private final AuthorityService authorityService;
    private final RoleAuthorityRepository roleAuthorityRepository;

    @Override
    public Page<RoleViewModel> searchRoles(SearchRolesQuery query) {
        return roleRepository.findAll(query.getPredicate(), query.getPageable()).map(RoleViewModel::from);
    }

    @Override
    public RoleViewModel getRole(long id) {
        Role role = validateRole(id);
        return RoleViewModel.from(role);
    }

    @Override
    public List<AuthorityViewModel> getRoleAuthorities(long roleId) {
        Role role = validateRole(roleId);
        List<Authority> roleAuthorities = authorityService.getRoleAuthorities(role);

        return roleAuthorities.stream()
                .map(AuthorityViewModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public RoleViewModel getRole(String name, String domain) {
        Role role = validateRole(name, domain);
        return RoleViewModel.from(role);
    }

    @Override
    public List<AuthorityViewModel> getRoleAuthorities(String name, String domain) {
        Role role = validateRole(name, domain);
        List<Authority> roleAuthorities = authorityService.getRoleAuthorities(role);

        return roleAuthorities.stream()
                .map(AuthorityViewModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public RoleViewModel createRole(CreateRoleRequest request) {
        CreateRoleRequestValidationResult validationResult = validateRoleCreationRequest(request);
        Role createdRole = createRole(request, validationResult);

        return RoleViewModel.from(createdRole);
    }

    private CreateRoleRequestValidationResult validateRoleCreationRequest(CreateRoleRequest request) {
        Domain domain = validateDomain(request.getDomain());
        validateRole(request.getName(), domain);
        List<Authority> authorities = authorityService.validateAuthorities(request.getAuthorities(), domain.getName());

        return CreateRoleRequestValidationResult.builder()
                .domain(domain)
                .authorities(authorities)
                .build();
    }

    private Domain validateDomain(String domain) {
        try {
            return domainService.validateDomain(domain);
        }
        catch (NotFoundException ex){
            throw new BadRequestException(ex.getErrorDescription());
        }
    }


    private void validateRole(String role, Domain domain) {
        if (roleRepository.existsByNameAndDomain(role, domain))
            throw new BadRequestException(String.format("Role with name: '%s' already exists", role));
    }

    private Role createRole(CreateRoleRequest request, CreateRoleRequestValidationResult validationResult) {
        Role role = buildRole(request, validationResult);
        Role createdRole = roleRepository.save(role);
        createRoleAuthorityMappings(createdRole, validationResult.getAuthorities());

        return createdRole;
    }

    private Role buildRole(CreateRoleRequest request, CreateRoleRequestValidationResult validationResult) {
        return Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .domain(validationResult.getDomain())
                .build();
    }

    @Override
    public List<Role> getUserRoles(User user) {
        return userRoleRepository.findByUser(user).stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList());
    }

    @Override
    public void addAuthoritiesToRole(long id, UpdateRoleAuthoritiesRequest request) {
        RoleAuthoritiesUpdateRequestValidationResult validationResult = validateRoleAuthoritiesUpdateRequest(id, request);
        addAuthoritiesToRole(validationResult);
    }

    @Override
    public void addAuthoritiesToRole(String role, String domain, UpdateRoleAuthoritiesRequest request) {
        RoleAuthoritiesUpdateRequestValidationResult validationResult = validateRoleAuthoritiesUpdateRequest(role, domain, request);
        addAuthoritiesToRole(validationResult);
    }

    private void addAuthoritiesToRole(RoleAuthoritiesUpdateRequestValidationResult validationResult) {
        var authorities = validationResult.getAuthorities().stream()
                .filter(authority -> !roleAuthorityRepository.existsByRoleAndAuthority(validationResult.getRole(), authority))
                .collect(Collectors.toList());

        createRoleAuthorityMappings(validationResult.getRole(), authorities);
    }

    @Override
    public void removeAuthoritiesFromRole(long id, UpdateRoleAuthoritiesRequest request) {
        RoleAuthoritiesUpdateRequestValidationResult validationResult = validateRoleAuthoritiesUpdateRequest(id, request);

        var authorities = validationResult.getAuthorities().stream()
                .filter(authority -> roleAuthorityRepository.existsByRoleAndAuthority(validationResult.getRole(), authority))
                .collect(Collectors.toList());

        deleteRoleAuthorityMappings(validationResult.getRole(), authorities);
    }

    @Override
    public void removeAuthoritiesFromRole(String role, String domain, UpdateRoleAuthoritiesRequest request) {
        RoleAuthoritiesUpdateRequestValidationResult validationResult = validateRoleAuthoritiesUpdateRequest(role, domain, request);

        var authorities = validationResult.getAuthorities().stream()
                .filter(authority -> roleAuthorityRepository.existsByRoleAndAuthority(validationResult.getRole(), authority))
                .collect(Collectors.toList());

        deleteRoleAuthorityMappings(validationResult.getRole(), authorities);
    }

    private RoleAuthoritiesUpdateRequestValidationResult validateRoleAuthoritiesUpdateRequest(long id, UpdateRoleAuthoritiesRequest request) {
        Role role = validateRole(id);
        List<Authority> authorities = authorityService.validateAuthorities(request.getAuthorities(), role.getDomain().getName());
        return buildRoleAuthoritiesUpdateRequestValidationResult(role, authorities);
    }

    private RoleAuthoritiesUpdateRequestValidationResult validateRoleAuthoritiesUpdateRequest(String roleName, String domain, UpdateRoleAuthoritiesRequest request) {
        Role role = validateRole(roleName, domain);
        List<Authority> authorities = authorityService.validateAuthorities(request.getAuthorities(), role.getDomain().getName());
        return buildRoleAuthoritiesUpdateRequestValidationResult(role, authorities);
    }

    private RoleAuthoritiesUpdateRequestValidationResult buildRoleAuthoritiesUpdateRequestValidationResult(Role role, List<Authority> authorities) {
        return RoleAuthoritiesUpdateRequestValidationResult.builder()
                .role(role)
                .authorities(authorities)
                .build();
    }

    private void createRoleAuthorityMappings(Role role, List<Authority> authorities) {
        List<RoleAuthority> roleAuthorities = new ArrayList<>(authorities.size());

        for (var authority : authorities) {
            RoleAuthority roleAuthority = RoleAuthority.builder().authority(authority).role(role).build();
            roleAuthorities.add(roleAuthority);
        }

        roleAuthorityRepository.saveAll(roleAuthorities);
    }

    private void deleteRoleAuthorityMappings(Role role, List<Authority> authorities) {
        var authorityIds = authorities.stream().map(Authority::getId).collect(Collectors.toList());
        roleAuthorityRepository.deleteByRoleAndAuthorityIn(role.getId(), authorityIds);
    }

    private Role validateRole(long id) {
        Optional<Role> roleExists = roleRepository.findById(id);

        if (roleExists.isEmpty())
            throw new NotFoundException(String.format("Role with id: '%d' does not exist", id));

        return roleExists.get();
    }

    private Role validateRole(String role, String domain) {
        Optional<Role> roleExists = roleRepository.findByNameAndDomainName(role, domain);

        if (roleExists.isEmpty())
            throw new NotFoundException(String.format("Role with name: %s does not exist in domain: %s", role, domain));

        return roleExists.get();
    }

    @Override
    public List<Role> validateRoles(List<String> roleNames, String domain) {
        List<Role> roles = roleRepository.findByNameInAndDomainName(roleNames, domain);

        if (roles.size() < roleNames.size()) {
            var foundRoles = roles.stream().map(Role::getName).collect(Collectors.toList());
            var rolesNotFound = roleNames.stream()
                    .filter(role -> !foundRoles.contains(role))
                    .collect(Collectors.toList());

            throw new BadRequestException(
                    String.format("The following roles sent: [%s] do not exist in the given domain: %s", String.join(", ", rolesNotFound), domain)
            );
        }

        return roles;
    }

}
