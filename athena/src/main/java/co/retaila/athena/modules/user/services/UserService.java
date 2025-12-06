package co.retaila.athena.modules.user.services;

import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.user.queries.SearchUsersQuery;
import co.retaila.athena.modules.user.requests.ChangeUserPasswordRequest;
import co.retaila.athena.modules.user.requests.CreateUserRequest;
import co.retaila.athena.modules.user.requests.RevokeUserAuthoritiesRequest;
import co.retaila.athena.modules.user.requests.UpdateUserRequest;
import co.retaila.athena.modules.user.requests.UpdateUserRolesRequest;
import co.retaila.athena.modules.user.viewmodels.UserViewModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    Page<UserViewModel> searchUsers(SearchUsersQuery query);
    UserViewModel getUser(Long id);
    UserViewModel getUser(String username, String domain);
    UserViewModel createUser(CreateUserRequest request);
    UserViewModel updateUser(Long id, UpdateUserRequest request);
    UserViewModel updateUser(String username, String domain, UpdateUserRequest request);
    void addRolesToUser(Long id, UpdateUserRolesRequest request);
    void addRolesToUser(String username, String domain, UpdateUserRolesRequest request);
    void removeRolesFromUser(Long id, UpdateUserRolesRequest request);
    void removeRolesFromUser(String username, String domain, UpdateUserRolesRequest request);
    List<AuthorityViewModel> getUserAuthorities(Long id);
    List<AuthorityViewModel> getUserAuthorities(String username, String domain);
    List<AuthorityViewModel> getRevokedAuthorities(Long id);
    List<AuthorityViewModel> getRevokedAuthorities(String username, String domain);
    void revokeAuthorities(Long id, RevokeUserAuthoritiesRequest request);
    void revokeAuthorities(String username, String domain, RevokeUserAuthoritiesRequest request);
    void permitRevokedAuthorities(Long id, RevokeUserAuthoritiesRequest request);
    void permitRevokedAuthorities(String username, String domain, RevokeUserAuthoritiesRequest request);
    void verifyUserEmail(Long id);
    void verifyUserEmail(String username, String domain);
    void changeUserPassword(ChangeUserPasswordRequest request);
    User authenticateUser(String username, String password, String domain);
    void validateUserDomain(User user, Domain domain);

}
