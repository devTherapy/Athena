package co.retaila.athena.modules.role.services;

import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.role.queries.SearchRolesQuery;
import co.retaila.athena.modules.role.requests.CreateRoleRequest;
import co.retaila.athena.modules.role.requests.UpdateRoleAuthoritiesRequest;
import co.retaila.athena.modules.role.viewmodels.RoleViewModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {

    Page<RoleViewModel> searchRoles(SearchRolesQuery query);
    RoleViewModel getRole(long id);
    RoleViewModel getRole(String name, String domain);
    List<AuthorityViewModel> getRoleAuthorities(long roleId);
    List<AuthorityViewModel> getRoleAuthorities(String name, String domain);
    RoleViewModel createRole(CreateRoleRequest request);
    List<Role> getUserRoles(User user);
    void addAuthoritiesToRole(long id, UpdateRoleAuthoritiesRequest request);
    void addAuthoritiesToRole(String role, String domain, UpdateRoleAuthoritiesRequest request);
    void removeAuthoritiesFromRole(long id, UpdateRoleAuthoritiesRequest request);
    void removeAuthoritiesFromRole(String role, String domain, UpdateRoleAuthoritiesRequest request);
    List<Role> validateRoles(List<String> roles, String domain);

}
