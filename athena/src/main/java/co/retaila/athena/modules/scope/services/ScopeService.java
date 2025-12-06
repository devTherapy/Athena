package co.retaila.athena.modules.scope.services;

import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.Scope;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.scope.queries.SearchScopesQuery;
import co.retaila.athena.modules.scope.queries.ValidateRequestedScopeQuery;
import co.retaila.athena.modules.scope.requests.CreateScopeRequest;
import co.retaila.athena.modules.scope.requests.UpdateScopeAuthoritiesRequest;
import co.retaila.athena.modules.scope.viewmodels.ScopeViewModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ScopeService {

    Page<ScopeViewModel> searchScopes(SearchScopesQuery query);
    ScopeViewModel getScope(long id);
    ScopeViewModel getScope(String name, String domain);
    List<AuthorityViewModel> getScopeAuthorities(long scopeId);
    List<AuthorityViewModel> getScopeAuthorities(String name, String domain);
    ScopeViewModel createScope(CreateScopeRequest request);
    void addAuthoritiesToScope(long id, UpdateScopeAuthoritiesRequest request);
    void addAuthoritiesToScope(String scope, String domain, UpdateScopeAuthoritiesRequest request);
    void removeAuthoritiesFromScope(long id, UpdateScopeAuthoritiesRequest request);
    void removeAuthoritiesFromScope(String scope, String domain, UpdateScopeAuthoritiesRequest request);
    List<ScopeViewModel> validateRequestedScope(ValidateRequestedScopeQuery query);
    List<Scope> validateRequestedScope(String requestedScope, Domain domain);
    List<Scope> validateOidcScopes(String requestedScope);
    List<Scope> validateRegularScopes(String requestedScope, Domain requestedDomain);

}
