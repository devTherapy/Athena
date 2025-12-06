package co.retaila.athena.modules.user.controllers;

import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.user.queries.SearchUsersQuery;
import co.retaila.athena.modules.user.requests.ChangeUserPasswordRequest;
import co.retaila.athena.modules.user.requests.CreateUserRequest;
import co.retaila.athena.modules.user.requests.RevokeUserAuthoritiesRequest;
import co.retaila.athena.modules.user.requests.UpdateUserRequest;
import co.retaila.athena.modules.user.requests.UpdateUserRolesRequest;
import co.retaila.athena.modules.user.services.UserService;
import co.retaila.athena.modules.user.viewmodels.UserViewModel;
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
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_CHANGE_USER_PASSWORD;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_CREATE_USER;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_SEARCH_USERS;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_UPDATE_USER;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_VERIFY_USER_EMAIL;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_VIEW_USER;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize(AUTH_PREFIX + CAN_SEARCH_USERS + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Page<UserViewModel> searchUsers(@Valid SearchUsersQuery query) {
        return userService.searchUsers(query);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public UserViewModel getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{username}/{domain}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public UserViewModel getUser(@PathVariable String username, @PathVariable String domain) {
        return userService.getUser(username, domain);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/authorities", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorityViewModel> getUserAuthorities(@PathVariable Long id) {
        return userService.getUserAuthorities(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{username}/{domain}/authorities", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorityViewModel> getUserAuthorities(@PathVariable String username, @PathVariable String domain) {
        return userService.getUserAuthorities(username, domain);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/revoked-authorities", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorityViewModel> getRevokedAuthorities(@PathVariable Long id) {
        return userService.getRevokedAuthorities(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{username}/{domain}/revoked-authorities", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorityViewModel> getRevokedAuthorities(@PathVariable String username, @PathVariable String domain) {
        return userService.getRevokedAuthorities(username, domain);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_CREATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public UserViewModel createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public UserViewModel updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{username}/{domain}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public UserViewModel updateUser(@PathVariable String username, @PathVariable String domain, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(username, domain, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/add-roles", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void addRolesToUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRolesRequest request) {
        userService.addRolesToUser(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{username}/{domain}/add-roles", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void addRolesToUser(@PathVariable String username, @PathVariable String domain, @Valid @RequestBody UpdateUserRolesRequest request) {
        userService.addRolesToUser(username, domain, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/remove-roles", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void removeRolesFromUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRolesRequest request) {
        userService.removeRolesFromUser(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{username}/{domain}/remove-roles", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void removeRolesFromUser(@PathVariable String username, @PathVariable String domain, @Valid @RequestBody UpdateUserRolesRequest request) {
        userService.removeRolesFromUser(username, domain, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/revoke-authorities", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void revokeAuthorities(@PathVariable Long id, @Valid @RequestBody RevokeUserAuthoritiesRequest request) {
        userService.revokeAuthorities(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{username}/{domain}/revoke-authorities", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void revokeAuthorities(@PathVariable String username, @PathVariable String domain, @Valid @RequestBody RevokeUserAuthoritiesRequest request) {
        userService.revokeAuthorities(username, domain, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/permit-revoked-authorities", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void permitRevokedAuthorities(@PathVariable Long id, @Valid @RequestBody RevokeUserAuthoritiesRequest request) {
        userService.permitRevokedAuthorities(id, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_UPDATE_USER + AUTH_SUFFIX)
    @RequestMapping(path = "/{username}/{domain}/permit-revoked-authorities", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void permitRevokedAuthorities(@PathVariable String username, @PathVariable String domain, @Valid @RequestBody RevokeUserAuthoritiesRequest request) {
        userService.permitRevokedAuthorities(username, domain, request);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VERIFY_USER_EMAIL + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}/verify-email", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void verifyUserEmail(@PathVariable Long id) {
        userService.verifyUserEmail(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VERIFY_USER_EMAIL + AUTH_SUFFIX)
    @RequestMapping(path = "/{username}/{domain}/verify-email", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void verifyUserEmail(@PathVariable String username, @PathVariable String domain) {
        userService.verifyUserEmail(username, domain);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_CHANGE_USER_PASSWORD + AUTH_SUFFIX)
    @RequestMapping(path = "/change-password", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void changeUserPassword(@Valid @RequestBody ChangeUserPasswordRequest request) {
        userService.changeUserPassword(request);
    }

}
