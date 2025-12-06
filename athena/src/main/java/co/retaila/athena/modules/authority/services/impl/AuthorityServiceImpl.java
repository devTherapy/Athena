package co.retaila.athena.modules.authority.services.impl;

import co.retaila.athena.common.constants.AuthorityConstants;
import co.retaila.athena.common.constants.DomainConstants;
import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.ClientAuthority;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.RoleAuthority;
import co.retaila.athena.common.entities.Scope;
import co.retaila.athena.common.entities.ScopeAuthority;
import co.retaila.athena.common.exceptions.BadRequestException;
import co.retaila.athena.common.exceptions.NotFoundException;
import co.retaila.athena.common.repositories.AuthorityRepository;
import co.retaila.athena.common.repositories.ClientAuthorityRepository;
import co.retaila.athena.common.repositories.RoleAuthorityRepository;
import co.retaila.athena.common.repositories.ScopeAuthorityRepository;
import co.retaila.athena.modules.authority.models.CreateAuthorityRequestValidationResult;
import co.retaila.athena.modules.authority.queries.SearchAuthoritiesQuery;
import co.retaila.athena.modules.authority.requests.CreateAuthorityRequest;
import co.retaila.athena.modules.authority.services.AuthorityService;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.domain.services.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final DomainService domainService;
    private final AuthorityRepository authorityRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;
    private final ClientAuthorityRepository clientAuthorityRepository;
    private final ScopeAuthorityRepository scopeAuthorityRepository;

    @Override
    public Page<AuthorityViewModel> searchAuthorities(SearchAuthoritiesQuery query) {
        return authorityRepository.findAll(query.getPredicate(), query.getPageable()).map(AuthorityViewModel::from);
    }

    @Override
    public AuthorityViewModel getAuthority(long id) {
        Authority authority = validateAuthority(id);
        return AuthorityViewModel.from(authority);
    }

    private Authority validateAuthority(Long id) {
        Optional<Authority> authorityExists = authorityRepository.findById(id);

        if (authorityExists.isEmpty()) {
            throw new NotFoundException(String.format("Authority with id: '%d' does not exist", id));
        }

        return authorityExists.get();
    }

    @Override
    public AuthorityViewModel getAuthority(String name, String domain) {
        Authority authority = validateAuthority(name, domain);
        return AuthorityViewModel.from(authority);
    }

    private Authority validateAuthority(String authority, String domain) {
        Optional<Authority> authorityExists = authorityRepository.findByNameAndDomainName(authority, domain);

        if (authorityExists.isEmpty()) {
            throw new NotFoundException(String.format("Authority with name: '%s' does not exist in domain: '%s'", authority, domain));
        }

        return authorityExists.get();
    }

    @Override
    public AuthorityViewModel createAuthority(CreateAuthorityRequest request) {
        CreateAuthorityRequestValidationResult validationResult = validateAuthorityCreationRequest(request);
        Authority authority = createAuthority(request, validationResult);
        return AuthorityViewModel.from(authority);
    }

    private CreateAuthorityRequestValidationResult validateAuthorityCreationRequest(CreateAuthorityRequest request) {
        validateAuthorityName(request.getName());
        Domain domain = validateDomain(request.getDomain());

        return CreateAuthorityRequestValidationResult.builder()
                .domain(domain)
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

    private void validateAuthorityName(String authority) {
        if (authorityRepository.existsByName(authority))
            throw new BadRequestException(String.format("Authority with name: '%s' already exists", authority));
    }

    private Authority createAuthority(CreateAuthorityRequest request, CreateAuthorityRequestValidationResult validationResult) {
        Authority authority = buildAuthority(request, validationResult);
        return authorityRepository.save(authority);
    }

    private Authority buildAuthority(CreateAuthorityRequest request, CreateAuthorityRequestValidationResult validationResult) {
        return Authority.builder()
                .name(request.getName())
                .description(request.getDescription())
                .domain(validationResult.getDomain())
                .build();
    }

    @Override
    public List<Authority> getRoleAuthorities(Role role) {
        return roleAuthorityRepository.findByRole(role).stream()
                .map(RoleAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    @Override
    public List<Authority> getScopeAuthorities(Scope scope) {
        return scopeAuthorityRepository.findByScope(scope).stream()
                .map(ScopeAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    @Override
    public List<Authority> getClientAuthorities(Client client) {
        return clientAuthorityRepository.findByClient(client).stream()
                .map(ClientAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    @Override
    public List<Authority> validateAuthorities(List<String> authorityNames, String domain) {
        List<Authority> authorities = authorityRepository.findByNameInAndDomainName(authorityNames, domain);

        validateAuthoritiesInDomain(authorities, authorityNames, domain);

        return authorities;
    }

    private void validateAuthoritiesInDomain(List<Authority> authorities, List<String> authorityNames, String domain) {
        if (authorities.size() < authorityNames.size()) {
            var foundAuthorities = authorities.stream().map(Authority::getName).collect(Collectors.toList());
            String authoritiesNotFound = authorityNames.stream()
                    .filter(authorityName -> !foundAuthorities.contains(authorityName))
                    .collect(Collectors.joining(", "));

            var errorMessageFormat = "The following authorities sent: [%s] do not exist in the given domain: %s";
            throw new NotFoundException(String.format(errorMessageFormat, authoritiesNotFound, domain));
        }
    }

    @Override
    public List<Authority> validateAuthorities(List<String> authorityNames, List<String> domains) {
        List<Authority> authorities = authorityRepository.findByNameInAndDomainNameIn(authorityNames, domains);

        validateAuthoritiesInDomains(authorities, authorityNames);

        return authorities;
    }

    private void validateAuthoritiesInDomains(List<Authority> authorities, List<String> authorityNames) {
        if (authorities.size() < authorityNames.size()) {
            var foundAuthorities = authorities.stream().map(Authority::getName).collect(Collectors.toList());
            String authoritiesNotFound = authorityNames.stream()
                    .filter(authorityName -> !foundAuthorities.contains(authorityName))
                    .collect(Collectors.joining(", "));

            var errorMessageFormat = "The following authorities sent: [%s] do not belong to any of the given domains";
            throw new NotFoundException(String.format(errorMessageFormat, authoritiesNotFound));
        }
    }

    @Override
    public List<Authority> getFirstPartyClientAuthorities() {
        return validateAuthorities(AuthorityConstants.getFirstPartyClientAuthorities(), DomainConstants.ATHENA);
    }

    @Override
    public boolean authorityExists(String authority, String domain) {
        return authorityRepository.existsByNameAndDomainName(authority, domain);
    }

}
