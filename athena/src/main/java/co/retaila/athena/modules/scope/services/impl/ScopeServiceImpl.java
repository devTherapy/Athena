package co.retaila.athena.modules.scope.services.impl;

import co.retaila.athena.common.constants.DomainConstants;
import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.Scope;
import co.retaila.athena.common.entities.ScopeAuthority;
import co.retaila.athena.common.enums.OidcScope;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.exceptions.BadRequestException;
import co.retaila.athena.common.exceptions.NotFoundException;
import co.retaila.athena.common.repositories.ScopeAuthorityRepository;
import co.retaila.athena.common.repositories.ScopeRepository;
import co.retaila.athena.modules.authority.services.AuthorityService;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.scope.models.CreateScopeRequestValidationResult;
import co.retaila.athena.modules.scope.models.ScopeAuthoritiesUpdateRequestValidationResult;
import co.retaila.athena.modules.scope.queries.SearchScopesQuery;
import co.retaila.athena.modules.scope.queries.ValidateRequestedScopeQuery;
import co.retaila.athena.modules.scope.requests.CreateScopeRequest;
import co.retaila.athena.modules.scope.requests.UpdateScopeAuthoritiesRequest;
import co.retaila.athena.modules.scope.services.ScopeService;
import co.retaila.athena.modules.scope.viewmodels.ScopeViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScopeServiceImpl implements ScopeService {

    private final ScopeRepository scopeRepository;
    private final DomainService domainService;
    private final AuthorityService authorityService;
    private final ScopeAuthorityRepository scopeAuthorityRepository;

    @Override
    public Page<ScopeViewModel> searchScopes(SearchScopesQuery query) {
        return scopeRepository.findAll(query.getPredicate(), query.getPageable()).map(ScopeViewModel::from);
    }

    @Override
    public ScopeViewModel getScope(long id) {
        Scope scope = validateScope(id);
        return ScopeViewModel.from(scope);
    }

    @Override
    public List<AuthorityViewModel> getScopeAuthorities(long scopeId) {
        Scope scope = validateScope(scopeId);
        List<Authority> scopeAuthorities = authorityService.getScopeAuthorities(scope);

        return scopeAuthorities.stream()
                .map(AuthorityViewModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public ScopeViewModel getScope(String name, String domain) {
        Scope scope = validateScope(name, domain);
        return ScopeViewModel.from(scope);
    }

    @Override
    public List<AuthorityViewModel> getScopeAuthorities(String name, String domain) {
        Scope scope = validateScope(name, domain);
        List<Authority> scopeAuthorities = authorityService.getScopeAuthorities(scope);

        return scopeAuthorities.stream()
                .map(AuthorityViewModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public ScopeViewModel createScope(CreateScopeRequest request) {
        CreateScopeRequestValidationResult validationResult = validateScopeCreationRequest(request);
        Scope createdScope = createScope(request, validationResult);

        return ScopeViewModel.from(createdScope);
    }

    private CreateScopeRequestValidationResult validateScopeCreationRequest(CreateScopeRequest request) {
        Domain domain = validateDomain(request.getDomain());
        validateScope(request.getName(), domain);
        List<Authority> authorities = authorityService.validateAuthorities(request.getAuthorities(), domain.getName());

        return CreateScopeRequestValidationResult.builder()
                .domain(domain)
                .authorities(authorities)
                .build();
    }

    private void validateScope(String scope, Domain domain) {
        if (OidcScope.getOidcScopes().contains(scope))
            throw new BadRequestException("Cannot give scope OIDC scope name");

        if (scopeRepository.existsByNameAndDomain(scope, domain))
            throw new BadRequestException(String.format("Scope with name: '%s' already exists", scope));
    }

    private Scope createScope(CreateScopeRequest request, CreateScopeRequestValidationResult validationResult) {
        Scope scope = buildScope(request, validationResult);
        Scope createdScope = scopeRepository.save(scope);
        createScopeAuthorityMappings(createdScope, validationResult.getAuthorities());

        return createdScope;
    }

    private Scope buildScope(CreateScopeRequest request, CreateScopeRequestValidationResult validationResult) {
        return Scope.builder()
                .name(request.getName())
                .description(request.getDescription())
                .domain(validationResult.getDomain())
                .build();
    }

    @Override
    public void addAuthoritiesToScope(long id, UpdateScopeAuthoritiesRequest request) {
        ScopeAuthoritiesUpdateRequestValidationResult validationResult = validateScopeAuthoritiesUpdateRequest(id, request);
        addAuthoritiesToScope(validationResult);
    }

    @Override
    public void addAuthoritiesToScope(String scope, String domain, UpdateScopeAuthoritiesRequest request) {
        ScopeAuthoritiesUpdateRequestValidationResult validationResult = validateScopeAuthoritiesUpdateRequest(scope, domain, request);
        addAuthoritiesToScope(validationResult);
    }

    private void addAuthoritiesToScope(ScopeAuthoritiesUpdateRequestValidationResult validationResult) {
        var authorities = validationResult.getAuthorities().stream()
                .filter(authority -> !scopeAuthorityRepository.existsByScopeAndAuthority(validationResult.getScope(), authority))
                .collect(Collectors.toList());

        createScopeAuthorityMappings(validationResult.getScope(), authorities);
    }

    @Override
    public void removeAuthoritiesFromScope(long id, UpdateScopeAuthoritiesRequest request) {
        ScopeAuthoritiesUpdateRequestValidationResult validationResult = validateScopeAuthoritiesUpdateRequest(id, request);

        var authorities = validationResult.getAuthorities().stream()
                .filter(authority -> scopeAuthorityRepository.existsByScopeAndAuthority(validationResult.getScope(), authority))
                .collect(Collectors.toList());

        deleteScopeAuthorityMappings(validationResult.getScope(), authorities);
    }

    @Override
    public void removeAuthoritiesFromScope(String scope, String domain, UpdateScopeAuthoritiesRequest request) {
        ScopeAuthoritiesUpdateRequestValidationResult validationResult = validateScopeAuthoritiesUpdateRequest(scope, domain, request);

        var authorities = validationResult.getAuthorities().stream()
                .filter(authority -> scopeAuthorityRepository.existsByScopeAndAuthority(validationResult.getScope(), authority))
                .collect(Collectors.toList());

        deleteScopeAuthorityMappings(validationResult.getScope(), authorities);
    }

    private ScopeAuthoritiesUpdateRequestValidationResult validateScopeAuthoritiesUpdateRequest(long id, UpdateScopeAuthoritiesRequest request) {
        Scope scope = validateScope(id);
        List<Authority> authorities = authorityService.validateAuthorities(request.getAuthorities(), scope.getDomain().getName());
        return buildScopeAuthoritiesUpdateRequestValidationResult(scope, authorities);
    }

    private ScopeAuthoritiesUpdateRequestValidationResult validateScopeAuthoritiesUpdateRequest(String scopeName, String domain, UpdateScopeAuthoritiesRequest request) {
        Scope scope = validateScope(scopeName, domain);
        List<Authority> authorities = authorityService.validateAuthorities(request.getAuthorities(), scope.getDomain().getName());
        return buildScopeAuthoritiesUpdateRequestValidationResult(scope, authorities);
    }

    private ScopeAuthoritiesUpdateRequestValidationResult buildScopeAuthoritiesUpdateRequestValidationResult(Scope scope, List<Authority> authorities) {
        return ScopeAuthoritiesUpdateRequestValidationResult.builder()
                .scope(scope)
                .authorities(authorities)
                .build();
    }

    private void createScopeAuthorityMappings(Scope scope, List<Authority> authorities) {
        List<ScopeAuthority> scopeAuthorities = new ArrayList<>(authorities.size());

        for (var authority : authorities) {
            ScopeAuthority scopeAuthority = ScopeAuthority.builder().authority(authority).scope(scope).build();
            scopeAuthorities.add(scopeAuthority);
        }

        scopeAuthorityRepository.saveAll(scopeAuthorities);
    }

    private void deleteScopeAuthorityMappings(Scope scope, List<Authority> authorities) {
        var authorityIds = authorities.stream().map(Authority::getId).collect(Collectors.toList());
        scopeAuthorityRepository.deleteByScopeAndAuthorityIn(scope.getId(), authorityIds);
    }

    private Scope validateScope(long id) {
        Optional<Scope> scopeExists = scopeRepository.findById(id);

        if (scopeExists.isEmpty())
            throw new NotFoundException(String.format("Scope with id: '%d' does not exist", id));

        return scopeExists.get();
    }

    private Scope validateScope(String scope, String domain) {
        Optional<Scope> scopeExists = scopeRepository.findByNameAndDomainName(scope, domain);

        if (scopeExists.isEmpty())
            throw new NotFoundException(String.format("Scope with name: %s does not exist in domain: %s", scope, domain));

        return scopeExists.get();
    }

    @Override
    public List<ScopeViewModel> validateRequestedScope(ValidateRequestedScopeQuery query) {
        Domain domain = validateDomain(query.getDomain());
        List<Scope> requestedScopeList = validateRequestedScope(query.getRequestedScope(), domain);

        return requestedScopeList.stream()
                .map(ScopeViewModel::from)
                .collect(Collectors.toList());
    }

    private Domain validateDomain(String domain) {
        try {
            return domainService.validateDomain(domain);
        }
        catch (NotFoundException ex){
            throw new BadRequestException(ex.getErrorDescription());
        }
    }


    @Override
    public List<Scope> validateRequestedScope(String requestedScope, Domain domain) {
        var response = new ArrayList<Scope>();

        response.addAll(validateOidcScopes(requestedScope));
        response.addAll(validateRegularScopes(requestedScope, domain));

        return response;
    }

    @Override
    public List<Scope> validateOidcScopes(String requestedScope) {
        List<String> requestedScopeList = List.of(requestedScope.split(" "));
        var oidcScopes = Arrays.stream(OidcScope.values())
                .filter(oidcScope -> requestedScopeList.contains(oidcScope.getValue()))
                .collect(Collectors.toList());

        if (!oidcScopes.isEmpty() && !oidcScopes.contains(OidcScope.OPENID)) {
            String errorDescription = String.format(
                    "'%s' scope required to request %s scope(s)",
                    OidcScope.OPENID.getValue(),
                    oidcScopes.stream().map(OidcScope::getValue).reduce((a, b)-> a + " and " + b)
            );
            throw new AuthException(ErrorConstants.INVALID_SCOPE, errorDescription);
        }

        return oidcScopes.stream()
                .map(this::buildOidcScope)
                .collect(Collectors.toList());
    }

    private Scope buildOidcScope(OidcScope oidcScope) {
        return Scope.builder()
                .name(oidcScope.getValue())
                .description(oidcScope.getDescription())
                .domain(DomainConstants.buildAthenaDomain())
                .build();
    }

    @Override
    public List<Scope> validateRegularScopes(String requestedScope, Domain domain) {
        var scopes = Arrays.stream(requestedScope.split(" "))
                .filter(scope -> !OidcScope.getOidcScopes().contains(scope))
                .collect(Collectors.toList());

        return validateScopes(scopes, domain);
    }

    private List<Scope> validateScopes(List<String> scopeNames, Domain domain) {
        List<Scope> scopes = scopeRepository.findByNameInAndDomain(scopeNames, domain);

        if (scopes.size() < scopeNames.size()) {
            var foundScopes = scopes.stream().map(Scope::getName).collect(Collectors.toList());
            var scopesNotFound = scopeNames.stream()
                    .filter(scope -> !foundScopes.contains(scope))
                    .collect(Collectors.toList());

            var errorDescription = String.format(
                    "The following scopes sent do not exist in the given domain: %s", String.join(", ", scopesNotFound)
            );
            throw new AuthException(ErrorConstants.INVALID_SCOPE, errorDescription);
        }

        return scopes;
    }

}
