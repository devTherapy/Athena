package co.retaila.athena.modules.user.services.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.BaseEntity;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.entities.UserRole;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.exceptions.BadRequestException;
import co.retaila.athena.common.exceptions.NotFoundException;
import co.retaila.athena.common.repositories.UserRepository;
import co.retaila.athena.common.repositories.UserRoleRepository;
import co.retaila.athena.modules.authority.services.AuthorityService;
import co.retaila.athena.modules.authority.viewmodels.AuthorityViewModel;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.revocation.services.RevocationService;
import co.retaila.athena.modules.role.services.RoleService;
import co.retaila.athena.modules.user.models.CreateUserRequestValidationResult;
import co.retaila.athena.modules.user.models.RevokeUserAuthoritiesRequestValidationResult;
import co.retaila.athena.modules.user.models.UserRolesUpdateRequestValidationResult;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityService authorityService;
    private final RoleService roleService;
    private final RevocationService revocationService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DomainService domainService;
    private final UserRoleRepository userRoleRepository;

    @Override
     public Page<UserViewModel> searchUsers(SearchUsersQuery query) {
        return userRepository.findAll(query.getPredicate(), query.getPageable()).map(UserViewModel::from);
    }

    @Override
    public UserViewModel getUser(Long id) {
        User user = validateUser(id);
        return UserViewModel.from(user);
    }

    private User validateUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null)
            throw new NotFoundException(String.format("User with id: %s does not exist", id));

        return user;
    }

    @Override
    public UserViewModel getUser(String username, String domain) {
        User user = validateUser(username, domain);
        return UserViewModel.from(user);
    }

    @Override
    public UserViewModel createUser(CreateUserRequest request) {
        var validationResult = validateUserCreationRequest(request);
        User user = createUser(request, validationResult);

        return UserViewModel.from(user);
    }

    private CreateUserRequestValidationResult validateUserCreationRequest(CreateUserRequest request) {
        Domain domain = validateDomain(request.getDomain());
        validateUsername(request.getUsername(), request.getDomain());
        List<Role> roles = roleService.validateRoles(request.getRoles(), domain.getName());

        return CreateUserRequestValidationResult.builder()
                .domain(domain)
                .roles(roles)
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

    private void validateUsername(String username, String domain) {
        if (userRepository.existsByUsernameAndDomainName(username, domain))
            throw new BadRequestException(String.format("User with username: %s already exists in given domain", username));
    }

    private User createUser(CreateUserRequest request, CreateUserRequestValidationResult validationResult) {
        User user = buildUser(request, validationResult);
        User createdUser = userRepository.save(user);
        createUserRoleMappings(createdUser, validationResult.getRoles());

        return createdUser;
    }

    private User buildUser(CreateUserRequest request, CreateUserRequestValidationResult validationResult) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .emailVerified(false)
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .domain(validationResult.getDomain())
                .build();
    }

    @Override
    public UserViewModel updateUser(Long id, UpdateUserRequest request) {
        User user = validateUser(id);

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());

        return UserViewModel.from(userRepository.save(user));
    }

    @Override
    public UserViewModel updateUser(String username, String domain, UpdateUserRequest request) {
        User user = validateUser(username, domain);

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());

        return UserViewModel.from(userRepository.save(user));
    }

    @Override
    public void addRolesToUser(Long id, UpdateUserRolesRequest request) {
        var validationResult = validateUserRolesUpdateRequest(id, request);

        var roles = validationResult.getRoles().stream()
                .filter(role -> !userRoleRepository.existsByUserAndRole(validationResult.getUser(), role))
                .collect(Collectors.toList());

        createUserRoleMappings(validationResult.getUser(), roles);
    }

    @Override
    public void addRolesToUser(String username, String domain, UpdateUserRolesRequest request) {
        var validationResult = validateUserRolesUpdateRequest(username, domain, request);

        var roles = validationResult.getRoles().stream()
                .filter(role -> !userRoleRepository.existsByUserAndRole(validationResult.getUser(), role))
                .collect(Collectors.toList());

        createUserRoleMappings(validationResult.getUser(), roles);
    }

    @Override
    public void removeRolesFromUser(Long id, UpdateUserRolesRequest request) {
        var validationResult = validateUserRolesUpdateRequest(id, request);

        var roles = validationResult.getRoles().stream()
                .filter(role -> userRoleRepository.existsByUserAndRole(validationResult.getUser(), role))
                .collect(Collectors.toList());

        deleteUserRoleMappings(validationResult.getUser(), roles);
    }

    @Override
    public void removeRolesFromUser(String username, String domain, UpdateUserRolesRequest request) {
        var validationResult = validateUserRolesUpdateRequest(username, domain, request);

        var roles = validationResult.getRoles().stream()
                .filter(role -> userRoleRepository.existsByUserAndRole(validationResult.getUser(), role))
                .collect(Collectors.toList());

        deleteUserRoleMappings(validationResult.getUser(), roles);
    }

    private UserRolesUpdateRequestValidationResult validateUserRolesUpdateRequest(Long id, UpdateUserRolesRequest request) {
        User user= validateUser(id);
        List<Role> roles = roleService.validateRoles(request.getRoles(), user.getDomain().getName());

        return UserRolesUpdateRequestValidationResult.builder()
                .user(user)
                .roles(roles)
                .build();
    }

    private UserRolesUpdateRequestValidationResult validateUserRolesUpdateRequest(String username, String domain, UpdateUserRolesRequest request) {
        User user= validateUser(username, domain);
        List<Role> roles = roleService.validateRoles(request.getRoles(), user.getDomain().getName());

        return UserRolesUpdateRequestValidationResult.builder()
                .user(user)
                .roles(roles)
                .build();
    }

    private void createUserRoleMappings(User user, List<Role> roles) {
        List<UserRole> userRoles = new ArrayList<>(roles.size());

        for (Role role : roles) {
            UserRole userRole = UserRole.builder().role(role).user(user).build();
            userRoles.add(userRole);
        }

        userRoleRepository.saveAll(userRoles);
    }

    private void deleteUserRoleMappings(User user, List<Role> roles) {
        var roleIds = roles.stream().map(BaseEntity::getId).collect(Collectors.toList());
        userRoleRepository.deleteByUserAndRoleIn(user.getId(), roleIds);
    }

    @Override
    public List<AuthorityViewModel> getUserAuthorities(Long id) {
        User user = validateUser(id);

        List<Role> roles = roleService.getUserRoles(user);
        List<String> revocations = getRevocations(user);
        List<Authority> authorities = getAuthorities(roles, revocations);

        return authorities.stream()
                .map(AuthorityViewModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuthorityViewModel> getUserAuthorities(String username, String domain) {
        User user = validateUser(username, domain);

        List<Role> roles = roleService.getUserRoles(user);
        List<String> revocations = getRevocations(user);
        List<Authority> authorities = getAuthorities(roles, revocations);

        return authorities.stream()
                .map(AuthorityViewModel::from)
                .collect(Collectors.toList());
    }

    private List<String> getRevocations(User user) {
        return revocationService.getUserRevocations(user).stream()
                .map(Authority::getName)
                .collect(Collectors.toList());
    }

    private List<Authority> getAuthorities(List<Role> roles, List<String> revocations) {
        var authorities = roles.stream()
                .map(authorityService::getRoleAuthorities)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return authorities.stream()
                .filter(authority -> !revocations.contains(authority.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuthorityViewModel> getRevokedAuthorities(Long id) {
        User user = validateUser(id);

        List<Authority> revocations = revocationService.getUserRevocations(user);

        return revocations.stream()
                .map(AuthorityViewModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuthorityViewModel> getRevokedAuthorities(String username, String domain) {
        User user = validateUser(username, domain);

        List<Authority> revocations = revocationService.getUserRevocations(user);

        return revocations.stream()
                .map(AuthorityViewModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public void revokeAuthorities(Long id, RevokeUserAuthoritiesRequest request) {
        var validationResult = validateRevokeUserAuthoritiesRequest(id, request);
        var authorities = revocationService.getAuthoritiesToRevoke(validationResult.getUser(), validationResult.getAuthorities());
        revocationService.createRevocations(validationResult.getUser(), authorities);
    }

    @Override
    public void revokeAuthorities(String username, String domain, RevokeUserAuthoritiesRequest request) {
        var validationResult = validateRevokeUserAuthoritiesRequest(username, domain, request);
        var authorities = revocationService.getAuthoritiesToRevoke(validationResult.getUser(), validationResult.getAuthorities());
        revocationService.createRevocations(validationResult.getUser(), authorities);
    }

    @Override
    public void permitRevokedAuthorities(Long id, RevokeUserAuthoritiesRequest request) {
        var validationResult = validateRevokeUserAuthoritiesRequest(id, request);
        var authorities = revocationService.getAuthoritiesToPermit(validationResult.getUser(), validationResult.getAuthorities());
        revocationService.deleteRevocations(validationResult.getUser(), authorities);
    }

    @Override
    public void permitRevokedAuthorities(String username, String domain, RevokeUserAuthoritiesRequest request) {
        var validationResult = validateRevokeUserAuthoritiesRequest(username, domain, request);
        var authorities = revocationService.getAuthoritiesToPermit(validationResult.getUser(), validationResult.getAuthorities());
        revocationService.deleteRevocations(validationResult.getUser(), authorities);
    }

    private RevokeUserAuthoritiesRequestValidationResult validateRevokeUserAuthoritiesRequest(Long id, RevokeUserAuthoritiesRequest request) {
        User user = validateUser(id);
        List<Authority> authorities = validateAuthorities(request, user);

        return RevokeUserAuthoritiesRequestValidationResult.builder()
                .user(user)
                .authorities(authorities)
                .build();
    }

    private RevokeUserAuthoritiesRequestValidationResult validateRevokeUserAuthoritiesRequest(String username, String domain, RevokeUserAuthoritiesRequest request) {
        User user = validateUser(username, domain);
        List<Authority> authorities = validateAuthorities(request, user);

        return RevokeUserAuthoritiesRequestValidationResult.builder()
                .user(user)
                .authorities(authorities)
                .build();
    }

    private List<Authority> validateAuthorities(RevokeUserAuthoritiesRequest request, User user) {
        try {
            return authorityService.validateAuthorities(request.getAuthorities(), user.getDomain().getName());
        }
        catch (NotFoundException ex) {
            throw new BadRequestException(ex.getErrorDescription());
        }
    }

    @Override
    public void verifyUserEmail(Long id) {
        User user = validateUser(id);
        verifyEmail(user);
    }

    @Override
    public void verifyUserEmail(String username, String domain) {
        User user = validateUser(username, domain);
        verifyEmail(user);
    }

    private void verifyEmail(User user) {
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    public void changeUserPassword(ChangeUserPasswordRequest request) {
        User user = validateUser(request.getUsername(), request.getDomain());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    private User validateUser(String username, String domain) {
        User user = userRepository.findByUsernameAndDomainName(username, domain).orElse(null);

        if (user == null)
            throw new NotFoundException(String.format("User with username: %s does not exist in given domain", username));

        return user;
    }

    @Override
    public User authenticateUser(String username, String password, String domain) {
        User user = userRepository.findByUsernameAndDomainName(username, domain).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword()))
            throw new AuthException(ErrorConstants.INVALID_GRANT, "Incorrect username or password");

        return user;
    }

    @Override
    public void validateUserDomain(User user, Domain domain) {
        if (!user.getDomain().getName().equals(domain.getName()))
            throw new AuthException(ErrorConstants.INVALID_GRANT, "User does not belong to given domain");
    }

}
