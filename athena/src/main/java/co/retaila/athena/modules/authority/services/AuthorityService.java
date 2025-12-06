package co.retaila.athena.modules.authority.services;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.Scope;
import co.retaila.athena.modules.authority.queries.SearchAuthoritiesQuery;
import co.retaila.athena.modules.authority.requests.CreateAuthorityRequest;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AuthorityService {

    Page<AuthorityViewModel> searchAuthorities(SearchAuthoritiesQuery query);
    AuthorityViewModel getAuthority(long id);
    AuthorityViewModel getAuthority(String name, String domain);
    AuthorityViewModel createAuthority(CreateAuthorityRequest request);
    List<Authority> getRoleAuthorities(Role role);
    List<Authority> getScopeAuthorities(Scope scope);
    List<Authority> getClientAuthorities(Client client);
    List<Authority> validateAuthorities(List<String> authorityNames, String domain);
    List<Authority> validateAuthorities(List<String> authorityNames, List<String> domains);
    List<Authority> getFirstPartyClientAuthorities();
    boolean authorityExists(String authority, String domain);

}
